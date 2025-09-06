package com.fingerprint.repositories;

import com.fingerprint.model.FingerPrintRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FingerPrintRecordRepository extends JpaRepository<FingerPrintRecord, Long> {
}
