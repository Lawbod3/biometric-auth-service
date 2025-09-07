package com.fingerprint.service;


import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerAlreadyRegisteredException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.exceptions.S3DownloadFailed;
import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrint;
import com.fingerprint.model.FingerPrintRecord;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import com.fingerprint.repositories.FingerPrintRepository;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FingerPrintServiceImpl implements FingerPrintService {
    private final FingerPrintRepository fingerPrintRepository;
    private final S3Client s3Client;
    private final FingerPrintRecordRepository fingerPrintRecordRepository;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;


    @Override
    public RegisterFingerPrintResponse processFingerPrintRegistration(String userId, String finger, List<FingerPrintRecord> records) {
        try {
            Finger fingerEnum = verifyFinger(userId, finger.toUpperCase());
            List<byte[]> imageBytesList = downloadImagesFromS3(records);
            List<FingerprintTemplate> extractedTemplates = extractFingerprintTemplates(imageBytesList);
            checkForDuplicateTemplatesAmongAllTemplates(extractedTemplates);

            FingerPrint entity = createFingerPrintEntity(userId, fingerEnum, extractedTemplates);
            fingerPrintRepository.save(entity);
            updateTemporaryRecords(records);

            log.info("Successfully registered fingerprint for user: {}, finger: {}", userId, finger);
            return createSuccessResponse();


        } catch (Exception e) {
            log.error("Fingerprint registration failed for user: {}, finger: {}", userId, finger, e);
            throw e;
        }

    }

    private  RegisterFingerPrintResponse createSuccessResponse() {
        RegisterFingerPrintResponse response = new RegisterFingerPrintResponse();
        response.setMessage("Fingerprint Registered successfully.");
        response.setSuccess(Boolean.TRUE);
        return response;
    }

    private  FingerPrint createFingerPrintEntity(String userId, Finger fingerEnum, List<FingerprintTemplate> extractedTemplates) {
        FingerPrint entity = new FingerPrint();
        entity.setUserId(userId);
        entity.setFinger(fingerEnum);
        entity.setTemplate(
                extractedTemplates.stream()
                        .map(FingerprintTemplate::toByteArray)
                        .toList()
        );
        return entity;
    }

    private  void checkForDuplicateTemplatesAmongAllTemplates(List<FingerprintTemplate> extractedTemplates) {
        List<FingerPrint> allFingerprints = fingerPrintRepository.findAll();
        for (FingerprintTemplate probeTemplate : extractedTemplates) {
            FingerprintMatcher matcher = new FingerprintMatcher(probeTemplate);
            for (FingerPrint candidate : allFingerprints) {
                for (byte[] stored : candidate.getTemplate()) {
                    FingerprintTemplate dbTemplate = new FingerprintTemplate(stored);
                    double score = matcher.match(dbTemplate);

                    if (score > 40) {
                        throw new FingerAlreadyRegisteredException("This fingerprint already exists in the system, linked to: " +
                                candidate.getUserId() + "-" + candidate.getFinger());
                    }

                }

            }

        }
    }

    private List<FingerprintTemplate> extractFingerprintTemplates(List<byte[]> imageBytesList) {
       List<FingerprintTemplate> extractedTemplates = imageBytesList.stream()
                .map(bytes -> new FingerprintTemplate(
                       new FingerprintImage()
                               .dpi(500)
                               .decode(bytes)
               ))
               .toList();
       return extractedTemplates;
   }

   private List<byte[]> downloadImagesFromS3(List<FingerPrintRecord> records) {
        return  records.stream()
                .map(record -> downloadImageFromS3(record.getS3Key()))
                .toList();
    }

    private byte[] downloadImageFromS3(String s3Key) {
        try{
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key).build();
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(request);
            log.debug("Successfully downloaded image from S3: {}", s3Key);
            return objectBytes.asByteArray();

        }
        catch (Exception e){
            log.error("Failed to download image from S3 with key: {}", s3Key, e);
            throw new S3DownloadFailed("Failed to download image from S3 with key: ");
        }

    }

    private Finger verifyFinger(String userId, String finger) throws FingerTypeDoesNotExistException {
        try {
           Finger fingerEnum = Finger.valueOf(finger.toUpperCase());
            fingerPrintRepository.findByUserIdAndFinger(userId, fingerEnum)
                    .ifPresent( fingerPrint -> {
                        throw new FingerAlreadyRegisteredByUserException("This finger is already registered for this user.");
                    });
            return fingerEnum;
        } catch (IllegalArgumentException  e) {
           throw new FingerTypeDoesNotExistException("Invalid finger type: " + finger);
        }

        }

    private void updateTemporaryRecords(List<FingerPrintRecord> records) {
        records.forEach(record -> {
            record.setUploadStatus("PROCESSED");
            record.setUploadedAt(Instant.now());
            fingerPrintRecordRepository.save(record);
        });

    }



   }

