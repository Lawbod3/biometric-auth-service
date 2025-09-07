package com.fingerprint.service;

import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Sql(scripts = {"/db/schema.sql"})
public class FingerprintServiceIntegrationTest {

    @Autowired
    private FingerPrintService fingerPrintService;

    @Test
    void testThatCanSaveFingerprint() throws IOException {
//        String imagePath = "/Users/sulaimonlawal/Downloads/fingerPrint/src/main/resources/fingerPrint.png";
//        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
//        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//
//        List<String> captures = List.of(base64Image, base64Image, base64Image);
//
//        RegisterFingerPrintRequest fingerPrintRequest = new RegisterFingerPrintRequest();
//        fingerPrintRequest.setFinger("LEFT_THUMB");
//        fingerPrintRequest.setFingerprintImageBase64(captures);
//        fingerPrintRequest.setUserId("1");
//        RegisterFingerPrintResponse fingerPrintResponse = fingerPrintService.processFingerPrintRegistration(fingerPrintRequest);
//        assertNotNull(fingerPrintResponse);


    }
}
