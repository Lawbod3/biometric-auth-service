package com.fingerprint.Repository;

import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrintRecord;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FingerPrintRecordRepositoryTest {

    @Autowired
    private FingerPrintRecordRepository fingerPrintRecordRepository;
    private FingerPrintRecord record;

    @BeforeEach
    void setUp() {
        fingerPrintRecordRepository.deleteAll();
         record = new FingerPrintRecord();
         record.setS3Key("key");
        record.setUserId("12345");
        record.setFinger(Finger.LEFT_INDEX);
    }

    @Test
    public void testRepositoryIsEmpty() {
        assertTrue(fingerPrintRecordRepository.findAll().isEmpty());
    }

    @Test
    public void testIsNotEmpty() {
        assertNull(record.getId());
       fingerPrintRecordRepository.save(record);
       assertNotNull(record.getId());
       assertTrue(fingerPrintRecordRepository.findById(record.getId()).isPresent());
    }

}
