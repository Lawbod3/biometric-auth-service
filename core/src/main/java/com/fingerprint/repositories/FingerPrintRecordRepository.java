package com.fingerprint.repositories;

import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrintRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FingerPrintRecordRepository extends JpaRepository<FingerPrintRecord, Long> {
    List<FingerPrintRecord> findByUserIdAndFingerAndUploadStatus(String userId, Finger finger, String pending);
}

