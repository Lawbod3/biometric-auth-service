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


}
