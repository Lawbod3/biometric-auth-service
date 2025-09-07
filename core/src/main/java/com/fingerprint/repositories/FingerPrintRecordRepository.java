package com.fingerprint.repositories;

import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrintRecord;
import com.fingerprint.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FingerPrintRecordRepository extends JpaRepository<FingerPrintRecord, Long> {
   List<FingerPrintRecord> findByUserIdAndFingerAndUploadStatus(String userId, Finger finger, Status status);

    boolean existsByUserIdAndFingerAndUploadStatus(String userId, Finger finger, Status status);

    List<FingerPrintRecord> findByUploadStatusAndCreatedAtBefore(Status status, Instant oneHourAgo);


    Status findByUserIdAndFinger(String userId, Finger finger);
}





