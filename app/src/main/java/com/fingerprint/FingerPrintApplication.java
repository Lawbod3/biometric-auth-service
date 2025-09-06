package com.fingerprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FingerPrintApplication {
    public static void main(String[] args) {
        SpringApplication.run(FingerPrintApplication.class, args);
    }
}
