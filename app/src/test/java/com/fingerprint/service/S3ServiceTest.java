package com.fingerprint.service;


import com.fingerprint.dto.response.PreSignedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class S3ServiceTest {
    @Autowired
    private S3Service s3Service;
    private PreSignedResponse preSignedResponse;



    @Test
    void  testS3Service() {
        String finger ="LEFT_LITTLE";
        String userId = "4567";
        assertTrue( s3Service.generatePreSignedUrl(userId, finger).isPresent());
    }

    @Test
    void  testS3ServiceThatPreSignedUrlsSizeIs10() {
        String finger ="LEFT_LITTLE";
        String userId = "4567";
        preSignedResponse =  s3Service.generatePreSignedUrl(userId, finger ).get();
        assertEquals( 10, preSignedResponse.getPreSignedUrls().size());
    }


}
