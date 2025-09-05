package com.fingerprint.service;

import com.fingerprint.dto.request.PreSignedUrlRequest;
import com.fingerprint.dto.response.PreSignedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class S3ServiceTest {
    @Autowired
    private S3Service s3Service;

    private PreSignedUrlRequest preSignedUrlRequest;

    private PreSignedResponse preSignedResponse;

    @BeforeEach
    void setUp() {
        preSignedUrlRequest = new PreSignedUrlRequest();
        preSignedUrlRequest.setFinger("LEFT_LITTLE");
        preSignedUrlRequest.setUserId("4567");
    }

    @Test
    void  testS3Service() {
        assertTrue( s3Service.getPreSignedUrl(preSignedUrlRequest).isPresent());

    }

    @Test
    void  testS3ServiceThatPreSignedUrlsSizeIs10() {
        preSignedResponse =  s3Service.getPreSignedUrl(preSignedUrlRequest).get();
        assertEquals( 10, preSignedResponse.getPreSignedUrls().size());
    }


}
