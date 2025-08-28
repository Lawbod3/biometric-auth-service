package com.fingerprint.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/db/schema.sql"})
public class FingerprintControllerTest {

     @Autowired
     private MockMvc mockMvc;

     @Test
     void testHomePage() throws Exception {
         String homePageEndpoint = "/api/Fingerprints/";
         mockMvc.perform(MockMvcRequestBuilders.get(homePageEndpoint)
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isOk())
                 .andExpect(MockMvcResultMatchers.content().string("Hello, Fingerprint App is running! ✅"))
                 .andDo(MockMvcResultHandlers.print());
     }

     @Test
     void testFingerprintCanRegister() throws Exception {
         String imagePath = "/Users/sulaimonlawal/IdeaProjects/Fingerprint/app/src/test/resources/images/testImage1.png";
         String imagePath2 = "/Users/sulaimonlawal/IdeaProjects/Fingerprint/app/src/test/resources/images/testImage2.png";
         byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
         byte[] imageBytes2 = Files.readAllBytes(Paths.get(imagePath2));
         String base64Image = Base64.getEncoder().encodeToString(imageBytes);
         String base64Image2 = Base64.getEncoder().encodeToString(imageBytes2);
         List<String> captures = List.of(base64Image, base64Image2);
         ObjectMapper objectMapper = new ObjectMapper();
         RegisterFingerPrintRequest fingerPrintRequest = new RegisterFingerPrintRequest();
         fingerPrintRequest.setFinger("LEFT_THUMB");
         fingerPrintRequest.setFingerprintImageBase64(captures);
         fingerPrintRequest.setUserId("2");
         String json = objectMapper.writeValueAsString(fingerPrintRequest);
         String registerFingerPrintEndpoint = "/api/Fingerprints/register";

         mockMvc.perform(MockMvcRequestBuilders.post(registerFingerPrintEndpoint)
         .contentType(MediaType.APPLICATION_JSON)
                 .content(json))
                 .andExpect(MockMvcResultMatchers.status().isCreated())
                 .andDo(MockMvcResultHandlers.print());

     }

    }
