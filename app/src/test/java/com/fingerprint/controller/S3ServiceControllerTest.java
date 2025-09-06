package com.fingerprint.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class S3ServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void testThatS3ServiceWork() throws Exception {
        mockMvc.perform(get("/getPreSignedUrl/")
                .param("userId", "12345") // Add query parameters
                .param("finger", "LEFT_THUMB"))
                .andExpect(status().isOk());
    }



}
