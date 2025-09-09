package com.fingerprint.service;

import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.exceptions.*;
import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrint;
import com.fingerprint.model.FingerPrintRecord;
import com.fingerprint.model.Status;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import com.fingerprint.repositories.FingerPrintRepository;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FingerprintServiceTest {

    @Mock
    private FingerPrintRepository fingerPrintRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private FingerPrintRecordRepository fingerPrintRecordRepository;

    @Mock
    private  TemplateExtractor templateExtractor;


    private static final String USER_ID = "987123";
    private static final String FINGER_TYPE = "LEFT_THUMB";
    private static final String S3_KEY = "s3-key-123";
    private static final byte[] IMAGE_BYTES = new byte[]{1, 2, 3, 4, 5};

    @Spy
    @InjectMocks
    private FingerPrintServiceImpl fingerPrintService;

    @Test
    void processFingerPrintRegistration_Success() {
        // Given
        FingerPrintRecord record = createFingerPrintRecord(Status.PENDING);
        List<FingerPrintRecord> records = List.of(record);

        // Mock S3 download
        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
        when(responseBytes.asByteArray()).thenReturn(IMAGE_BYTES);
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        // Mock repository calls
        when(fingerPrintRepository.findByUserIdAndFinger(USER_ID, Finger.LEFT_THUMB))
                .thenReturn(Optional.empty());
        when(fingerPrintRecordRepository.save(any(FingerPrintRecord.class))).thenReturn(record);
        when(fingerPrintRepository.save(any(FingerPrint.class))).thenReturn(new FingerPrint());

        // Mock template extractor
        FingerprintTemplate mockTemplate = mock(FingerprintTemplate.class);
        when(mockTemplate.toByteArray()).thenReturn(new byte[]{1, 2, 3});
        when(templateExtractor.extractTemplates(anyList())).thenReturn(List.of(mockTemplate));

        // Stub duplicate check (void method → doNothing)
        doNothing().when(fingerPrintService).checkForDuplicateTemplatesAmongAllTemplates(anyList());

        // When
        assertDoesNotThrow(() -> fingerPrintService.processFingerPrintRegistration(USER_ID, FINGER_TYPE, records));

        // Then
        verify(fingerPrintRepository, times(1)).findByUserIdAndFinger(USER_ID, Finger.LEFT_THUMB);
        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
        verify(fingerPrintRepository, times(1)).save(any(FingerPrint.class));
        verify(fingerPrintRecordRepository, times(1)).save(any(FingerPrintRecord.class));

        // Verify record was updated to REGISTERED
        assertEquals(Status.REGISTERED, record.getUploadStatus());
        assertNotNull(record.getUploadedAt());
    }



    @Test
    void processFingerPrintRegistration_InvalidFingerType_ThrowsException() {
        // Given
        String invalidFingerType = "INVALID_FINGER";
        List<FingerPrintRecord> records = List.of(createFingerPrintRecord(Status.PENDING));

        // When & Then
        assertThrows(FingerTypeDoesNotExistException.class,
                () -> fingerPrintService.processFingerPrintRegistration(USER_ID, invalidFingerType, records));

        verify(fingerPrintRepository, never()).save(any(FingerPrint.class));
        verify(fingerPrintRecordRepository, never()).save(any(FingerPrintRecord.class));
    }

    @Test
    void processFingerPrintRegistration_AlreadyRegisteredForUser_ThrowsException() {
        // Given
        FingerPrint existingFingerPrint = new FingerPrint();
        existingFingerPrint.setUserId(USER_ID);
        existingFingerPrint.setFinger(Finger.LEFT_THUMB);

        List<FingerPrintRecord> records = List.of(createFingerPrintRecord(Status.PENDING));

        when(fingerPrintRepository.findByUserIdAndFinger(USER_ID, Finger.LEFT_THUMB))
                .thenReturn(Optional.of(existingFingerPrint));

        // When & Then
        assertThrows(FingerAlreadyRegisteredByUserException.class,
                () -> fingerPrintService.processFingerPrintRegistration(USER_ID, FINGER_TYPE, records));

        verify(fingerPrintRepository, never()).save(any(FingerPrint.class));
        verify(fingerPrintRecordRepository, never()).save(any(FingerPrintRecord.class));
    }

    @Test
    void processFingerPrintRegistration_S3DownloadFails_ThrowsException() {
        // Given
        List<FingerPrintRecord> records = List.of(createFingerPrintRecord(Status.PENDING));

        when(fingerPrintRepository.findByUserIdAndFinger(USER_ID, Finger.LEFT_THUMB))
                .thenReturn(Optional.empty());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 error"));

        // When & Then
        assertThrows(S3DownloadFailed.class,
                () -> fingerPrintService.processFingerPrintRegistration(USER_ID, FINGER_TYPE, records));

        verify(fingerPrintRepository, never()).save(any(FingerPrint.class));
        verify(fingerPrintRecordRepository, never()).save(any(FingerPrintRecord.class));
    }

    private FingerPrintRecord createFingerPrintRecord(Status status) {
        FingerPrintRecord record = new FingerPrintRecord();
        record.setUserId(USER_ID);
        record.setFinger(Finger.LEFT_THUMB);
        record.setS3Key(S3_KEY);
        record.setUploadStatus(status);
        if (status == Status.REGISTERED) {
            record.setUploadedAt(Instant.now());
        }
        return record;
    }
}