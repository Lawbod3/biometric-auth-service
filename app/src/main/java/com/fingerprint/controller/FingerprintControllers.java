package com.fingerprint.controller;

import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.ApiResponse;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerAlreadyRegisteredException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
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


    @GetMapping("/")
    public String home() {
        return "Hello, Fingerprint App is running! ✅";
    }


    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
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
