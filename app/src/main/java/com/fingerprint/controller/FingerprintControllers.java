package com.fingerprint.controller;

import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.ApiResponse;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerAlreadyRegisteredException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.service.FingerPrintService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Fingerprints")
public class FingerprintControllers {

    private final FingerPrintService fingerPrintService;
    public FingerprintControllers(FingerPrintService fingerPrintService) {
        this.fingerPrintService = fingerPrintService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterFingerPrintRequest request) {
        try{
            RegisterFingerPrintResponse response = fingerPrintService.register(request);
            return new ResponseEntity<>(new ApiResponse(true, response), HttpStatus.CREATED);

        }
        catch (FingerTypeDoesNotExistException | FingerAlreadyRegisteredException | FingerAlreadyRegisteredByUserException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
