package com.fingerprint.service;

import com.fingerprint.dto.response.PreSignedResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerPrintRecordNotFound;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrintRecord;
import com.fingerprint.model.Status;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import com.fingerprint.repositories.FingerPrintRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final FingerPrintRepository fingerPrintRepository;
    private final S3Presigner s3Presigner;
    private final FingerPrintService fingerPrintService;
    private final S3Client s3Client;
    private final TaskExecutor taskExecutor;
    private final FingerPrintRecordRepository fingerPrintRecordRepository;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    public S3ServiceImpl(
            FingerPrintRepository fingerPrintRepository,
            S3Presigner s3Presigner,
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor,
            FingerPrintService fingerPrintService,
            S3Client s3Client,
            FingerPrintRecordRepository fingerPrintRecordRepository
    ) {
        this.fingerPrintRepository = fingerPrintRepository;
        this.s3Presigner = s3Presigner;
        this.taskExecutor = taskExecutor;
        this.fingerPrintService = fingerPrintService;
        this.s3Client = s3Client;
        this.fingerPrintRecordRepository = fingerPrintRecordRepository;
    }




    @Override
    public Optional<PreSignedResponse> generatePreSignedUrl(String userId, String finger) {
        cleanupPendingUploads(userId,Finger.valueOf(finger.toUpperCase()));
        if (fingerPrintRecordRepository.existsByUserIdAndFingerAndUploadStatus(userId, Finger.valueOf(finger.toUpperCase()), Status.PENDING)) {
            throw new FingerAlreadyRegisteredByUserException("Fingerprint already registered for this user");
        }
        PreSignedResponse preSignedResponse = new PreSignedResponse();
         verifyFinger(userId, finger);
        List<String> urls = getPreSignedUrl(userId, finger);
        preSignedResponse.setPreSignedUrls(urls);
      return Optional.of(preSignedResponse);
    }


    private List<String> getPreSignedUrl(String userId, String finger) {
        List<String> urls = IntStream.range(0,10)
                .mapToObj(i ->{
                    String key ="fingerprints/" + userId + "/" + finger + "/" + UUID.randomUUID() + ".png";
                    saveFingerPrintRecord(userId, finger, key);

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

                    PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(10))
                            .putObjectRequest(putObjectRequest)
                            .build();

                    return s3Presigner.presignPutObject(preSignRequest).url().toString();
                })
                .toList();
        return urls;
    }

    private void saveFingerPrintRecord(String userId, String finger, String key) {
        FingerPrintRecord record = new FingerPrintRecord();
        record.setUserId(userId);
        record.setS3Key(key);
        record.setFinger(Finger.valueOf(finger.toUpperCase()));
        record.setUploadStatus(Status.PENDING);
        record.setCreatedAt(Instant.now());
        fingerPrintRecordRepository.save(record);
    }

    private void cleanupPendingUploads(String userId, Finger finger) {
        fingerPrintRecordRepository
                .findByUserIdAndFingerAndUploadStatus(userId, finger, Status.PENDING)
                .stream()
                .forEach(record -> {
                    try {
                        deleteObjectFromS3(record.getS3Key());
                    } catch (Exception e) {
                        log.debug("S3 object did not exist or could not be deleted: {}", record.getS3Key());
                    }
                    fingerPrintRecordRepository.delete(record);
                });
    }

    public void deleteObjectFromS3(String s3Key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key).build();
            s3Client.deleteObject(request);

        }catch (Exception e) {
            throw e;
        }
    }


    private void verifyFinger(String userId, String finger) {
        try {
            Finger fingerEnum = Finger.valueOf(finger.toUpperCase());
            fingerPrintRepository.findByUserIdAndFinger(userId, fingerEnum)
                    .ifPresent( fingerPrint -> {
                        throw new FingerAlreadyRegisteredByUserException("This finger is already registered for this user.");
                    });

        } catch (IllegalArgumentException | FingerTypeDoesNotExistException  e) {
            throw new FingerTypeDoesNotExistException("Invalid finger type: " + finger);
        }

    }

    @Override
    public void confirmSuccessfulUpload(String userId, String finger) {
        verifyFinger(userId, finger);
       List<FingerPrintRecord>  records = fingerPrintRecordRepository
                .findByUserIdAndFingerAndUploadStatus(userId, Finger.valueOf(finger.toUpperCase()),  Status.PENDING);
        if (records.isEmpty()) {
            throw new FingerPrintRecordNotFound("No pending records");
        }

        List<FingerPrintRecord>updatedRecords = records.stream()
                .map(eachRecord->{
                    eachRecord.setUploadStatus(Status.SUCCESS);
                    eachRecord.setUploadedAt(Instant.now());
                    return fingerPrintRecordRepository.save(eachRecord);
                })
                .toList();
        triggerAsyncRegistration(userId,finger, updatedRecords);
    }

    private void triggerAsyncRegistration(String userId, String finger, List<FingerPrintRecord> fingerPrintRecords) {
            taskExecutor.execute(() ->{
                try{
                    fingerPrintService.processFingerPrintRegistration(userId, finger, fingerPrintRecords);

                } catch (Exception e) {
                    log.error("Async registration failed for user: {}, finger: {}", userId, finger, e);
                    updateRecordsStatus(fingerPrintRecords, Status.FAILED);
                }
            });

    }

    private void updateRecordsStatus(List<FingerPrintRecord> records, Status status) {
        records.forEach(eachRecord -> {
            eachRecord.setUploadStatus(status);
            fingerPrintRecordRepository.save(eachRecord);
        });
    }



}
