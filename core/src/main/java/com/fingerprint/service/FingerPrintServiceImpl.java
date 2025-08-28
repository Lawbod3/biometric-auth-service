package com.fingerprint.service;


import com.fingerprint.dto.request.RegisterFingerPrintRequest;
import com.fingerprint.dto.response.RegisterFingerPrintResponse;
import com.fingerprint.exceptions.FingerAlreadyRegisteredByUserException;
import com.fingerprint.exceptions.FingerAlreadyRegisteredException;
import com.fingerprint.exceptions.FingerTypeDoesNotExistException;
import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrint;
import com.fingerprint.repositories.FingerPrintRepository;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FingerPrintServiceImpl implements FingerPrintService {
    private final FingerPrintRepository fingerPrintRepository;


    @Override
    public RegisterFingerPrintResponse register(RegisterFingerPrintRequest request) {
        Finger fingerEnum = verifyFinger(request);
        List<byte[]> imageBytesList = convertImageBase64ToBytes(request);
        List<FingerprintTemplate> extractedTemplates = extractFingerprintTemplates(imageBytesList);
        List<FingerPrint> allFingerprints = fingerPrintRepository.findAll();
        for(FingerprintTemplate probeTemplate : extractedTemplates) {
            FingerprintMatcher matcher = new FingerprintMatcher(probeTemplate);
            for(FingerPrint candidate : allFingerprints) {
                for(byte[] stored : candidate.getTemplate()){
                    FingerprintTemplate dbTemplate = new FingerprintTemplate(stored);
                    double score = matcher.match(dbTemplate);

                    if(score > 40){
                        throw new FingerAlreadyRegisteredException( "This fingerprint already exists in the system, linked to: " +
                                candidate.getUserId() + "-" + candidate.getFinger());
                    }

                }

            }

        }

        FingerPrint entity = new FingerPrint();
        entity.setUserId(request.getUserId());
        entity.setFinger(fingerEnum);
        entity.setTemplate(
                extractedTemplates.stream()
                        .map(FingerprintTemplate::toByteArray)
                        .toList()
        );

        fingerPrintRepository.save(entity);
        RegisterFingerPrintResponse response = new RegisterFingerPrintResponse();
        response.setMessage("Fingerprint Registered successfully.");
        response.setSuccess(Boolean.TRUE);
        return response;

    }

    private List<FingerprintTemplate> extractFingerprintTemplates(List<byte[]> imageBytesList) {
        List<FingerprintTemplate> extractedTemplates = imageBytesList.stream()
                .map(bytes -> new FingerprintTemplate(
                        new FingerprintImage()
                                .dpi(500)
                                .decode(bytes)
                ))
                .toList();
        return extractedTemplates;
    }

    private List<byte[]> convertImageBase64ToBytes(RegisterFingerPrintRequest request) {
        List<byte[]> imageBytesList = request.getFingerprintImageBase64().stream()
                .filter(Objects::nonNull)
                .map(imgBase64 -> Base64.getDecoder().decode(imgBase64))
                .toList();
        if (imageBytesList.isEmpty())throw new IllegalArgumentException("No valid fingerprint images provided.");
        return imageBytesList;
    }

    private Finger verifyFinger(RegisterFingerPrintRequest request) {
        try {
           Finger fingerEnum = Finger.valueOf(request.getFinger().toUpperCase());
            fingerPrintRepository.findByUserIdAndFinger(request.getUserId(), fingerEnum)
                    .ifPresent( fingerPrint -> {
                        throw new FingerAlreadyRegisteredByUserException("This finger is already registered for this user.");
                    });
            return fingerEnum;
        } catch (IllegalArgumentException  e) {
           throw new FingerTypeDoesNotExistException("Invalid finger type: " + request.getFinger());
        }

        }



   }

