package com.fingerprint.controller;

import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.ApiResponse;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerAlreadyRegisteredException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.model.Finger;
import com.fingerprint.repositories.FingerPrintRecordRepository;
import com.fingerprint.service.FingerPrintService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Fingerprints")
@AllArgsConstructor
public class FingerprintControllers {

    private final FingerPrintService fingerPrintService;
    private final FingerPrintRecordRepository fingerPrintRecordRepository;


    @GetMapping("/")
    public String home() {
        return "Hello, Fingerprint App is running! ✅";
    }


    @GetMapping (
            value = "/register-status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> register(@RequestParam String userId, @RequestParam String finger) {
        try{
           String status = fingerPrintRecordRepository.findByUserIdAndFinger(userId, Finger.valueOf(finger));
            return new ResponseEntity<>(new ApiResponse(true, status), HttpStatus.OK);

        }
        catch (FingerTypeDoesNotExistException | FingerAlreadyRegisteredException | FingerAlreadyRegisteredByUserException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
