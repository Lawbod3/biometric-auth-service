package com.fingerprint.dto.response;

public class RegisterFingerPrintResponse {
    private boolean success = false;
    private String message;

    public RegisterFingerPrintResponse(String message) {
        this.success = true;
        this.message = message;
    }
}
