package com.fingerprint.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreSignedUrlRequest {
    private String userId;
    private String finger;
}
