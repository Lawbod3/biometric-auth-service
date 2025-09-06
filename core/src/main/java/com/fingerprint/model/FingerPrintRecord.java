package com.fingerprint.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "Fingerprint_records")
@Getter
@Setter
public class FingerPrintRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String S3Key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Finger finger;

    private String uploadStatus;

    private Instant uploadedAt;
    private Instant createdAt;




}
