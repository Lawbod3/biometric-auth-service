package com.fingerprint.service;


import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.model.FingerPrintRecord;

import java.util.List;

public interface FingerPrintService {
    void processFingerPrintRegistration(String userId, String finger, List<FingerPrintRecord> records);
}
