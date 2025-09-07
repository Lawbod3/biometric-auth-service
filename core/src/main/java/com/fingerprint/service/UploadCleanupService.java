package com.fingerprint.service;

import com.fingerprint.model.Status;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadCleanupService {
    private final FingerPrintRecordRepository fingerprintRecordRepository;
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupService(){
        log.info("Running cleanup job for abandoned uploads...");
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        long deletedCount = fingerprintRecordRepository.
                findByUploadStatusAndCreatedAtBefore(Status.PENDING, oneHourAgo)
                .stream()
                .map(record -> {
                    try {
                        s3Service.deleteObjectFromS3(record.getS3Key());
                    } catch (Exception e) {
                        log.debug("Could not delete S3 object (might not exist): {}", record.getS3Key());
                    }
                    fingerprintRecordRepository.delete(record);
                    log.debug("Cleaned up abandoned record: {}", record.getS3Key());
                    return record;
                })
                .count();

        log.info("Deleted {} abandoned records", deletedCount);
    }
}
