package com.fingerprint.controller;


import com.fingerprint.dto.response.PreSignedResponse;
import com.fingerprint.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
//@WebMvcTest(S3ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class S3ServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    //@MockBean
    //private S3Service s3Service;


    @Test
    void testThatS3ServiceWork() throws Exception {
//        PreSignedResponse response = new PreSignedResponse(
//        );
//        response.setPreSignedUrls( List.of("https://s3.com/some-url-1", "https://s3.com/some-url-2"));
//        when(s3Service.generatePreSignedUrl("12345", "LEFT_THUMB"))
 //               .thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/Fingerprints/getPreSignedUrl")
                .param("userId", "12345") // Add query parameters
                .param("finger", "LEFT_THUMB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }



}
