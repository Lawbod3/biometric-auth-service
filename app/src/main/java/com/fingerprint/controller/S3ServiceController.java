package com.fingerprint.controller;


import com.fingerprint.dto.response.ApiResponse;
import com.fingerprint.dto.response.PreSignedResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Fingerprints")
@AllArgsConstructor
public class S3ServiceController {

    private final S3Service s3Service;

    @GetMapping("/getPreSignedUrl")
    public ResponseEntity<?> getPreSignedUrl(@RequestParam String userId, @RequestParam String finger) {
      try{
          PreSignedResponse response = s3Service.generatePreSignedUrl(userId, finger)
                  .orElseThrow(() -> new RuntimeException("Failed to generate pre-signed URLs"));
          return new ResponseEntity<>(new ApiResponse(true, response), HttpStatus.OK);
      }
      catch (FingerAlreadyRegisteredByUserException | FingerTypeDoesNotExistException e){
          return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
      }catch (Exception e) {
          return new ResponseEntity<>(new ApiResponse(false, "Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
      }

    }

    @PostMapping
    public ResponseEntity<?> confirmImageUploadOnS3(@RequestParam String userId, @RequestParam String finger){
        try{
            s3Service.confirmSuccessfulUpload(userId, finger);
            return ResponseEntity.ok(new ApiResponse(true,
                    "Upload confirmed and registration processing started"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to confirm upload: " + e.getMessage()));
        }


    }



}
