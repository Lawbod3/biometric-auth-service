package com.fingerprint.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class FingerPrint {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userId;

    @ElementCollection
    @CollectionTable(name = "fingerprint_templates", joinColumns = @JoinColumn(name = "fingerprint_id"))
    @Column(name = "template", columnDefinition = "BYTEA")
    private List<byte[]> template = new ArrayList<>();




    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Finger finger;

//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public List<byte[]> getTemplate() {
//        return template;
//    }
//
//    public void setTemplate(List<byte[]> template) {
//        this.template = template;
//    }
//
//    public Finger getFinger() {
//        return finger;
//    }
//
//    public void setFinger(Finger finger) {
//        this.finger = finger;
//    }
}
