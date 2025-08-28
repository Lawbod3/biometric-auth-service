package com.fingerprint.service;


import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;

public interface FingerPrintService {
    RegisterFingerPrintResponse register(RegisterFingerPrintRequest fingerPrintRequest);
}
