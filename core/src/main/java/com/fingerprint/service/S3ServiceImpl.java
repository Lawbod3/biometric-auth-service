package com.fingerprint.service;

import com.fingerprint.dto.response.PreSignedResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.model.Finger;
import com.fingerprint.repositories.FingerPrintRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service

public class S3ServiceImpl implements S3Service {
    private final FingerPrintRepository fingerPrintRepository;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public S3ServiceImpl(FingerPrintRepository fingerPrintRepository, S3Presigner s3Presigner) {
        this.fingerPrintRepository = fingerPrintRepository;
        this.s3Presigner = s3Presigner;
    }


    @Override
    public Optional<PreSignedResponse> getPreSignedUrl(String userId, String finger) {
        PreSignedResponse preSignedResponse = new PreSignedResponse();
         verifyFinger(userId, finger);
        List<String> urls = IntStream.range(0,10)
                .mapToObj(i ->{
                    String key ="fingerprints/" + userId + "/" + finger + "/" + UUID.randomUUID() + ".png";
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

                    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(10))
                            .putObjectRequest(putObjectRequest)
                            .build();

                    return s3Presigner.presignPutObject(presignRequest).url().toString();
                })
                .toList();
        preSignedResponse.setPreSignedUrls(urls);

      return Optional.of(preSignedResponse);
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
}
