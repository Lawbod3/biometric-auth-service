package com.fingerprint.service;


import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrintRecord;
import com.fingerprint.model.Status;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import com.fingerprint.repositories.FingerPrintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {
    @Mock
    private FingerPrintRecordRepository fingerPrintRecordRepository;

    @Mock
    private FingerPrintRepository fingerPrintRepository;

    @Mock
    private FingerPrintService fingerPrintService;

    @Mock
    private TaskExecutor taskExecutor;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    void confirmSuccessfulUpload_WithPendingRecords_UpdatesStatusAndTriggersAsync() {

        String userId = "user123";
        String finger = "LEFT_THUMB";

        FingerPrintRecord pendingRecord = new FingerPrintRecord();
        pendingRecord.setUserId(userId);
        pendingRecord.setFinger(Finger.LEFT_THUMB);
        pendingRecord.setS3Key("s3-key-123");
        pendingRecord.setUploadStatus(Status.PENDING);

        when(fingerPrintRepository.findByUserIdAndFinger(userId, Finger.valueOf(finger.toUpperCase())))
                .thenReturn(Optional.empty());

        when(fingerPrintRecordRepository.findByUserIdAndFingerAndUploadStatus(
                userId, Finger.valueOf(finger.toUpperCase()), Status.PENDING))
                .thenReturn(List.of(pendingRecord));

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        s3Service.confirmSuccessfulUpload(userId, finger);

        assertEquals(Status.SUCCESS, pendingRecord.getUploadStatus());
        assertNotNull(pendingRecord.getUploadedAt());

        verify(fingerPrintRecordRepository, times(1)).save(pendingRecord);

        verify(taskExecutor).execute(runnableCaptor.capture());

        Runnable asyncTask = runnableCaptor.getValue();
        assertNotNull(asyncTask);
    }





}
