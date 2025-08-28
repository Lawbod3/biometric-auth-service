package com.fingerprint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class RegisterFingerPrintResponse {
    private boolean success ;
    private String message;


}
