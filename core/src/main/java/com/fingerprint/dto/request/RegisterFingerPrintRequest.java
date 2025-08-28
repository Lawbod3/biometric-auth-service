package com.fingerprint.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterFingerPrintRequest {

    private String userId;
    private String finger;
    private List<String> fingerprintImageBase64;


//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getFinger() {
//        return finger;
//    }
//
//    public void setFinger(String finger) {
//        this.finger = finger;
//    }
//
//    public List<String> getFingerprintImageBase64() {
//        return fingerprintImageBase64;
//    }
//
//    public void setFingerprintImageBase64(List<String> fingerprintImageBase64) {
//        this.fingerprintImageBase64 = fingerprintImageBase64;
//    }
}
