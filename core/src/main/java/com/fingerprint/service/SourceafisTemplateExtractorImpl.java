package com.fingerprint.service;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SourceafisTemplateExtractorImpl implements TemplateExtractor {
    @Override
    public List<FingerprintTemplate> extractTemplates(List<byte[]> imageBytesList) {
        return imageBytesList.stream()
                .map(bytes -> new FingerprintTemplate(
                        new FingerprintImage()
                                .dpi(500)
                                .decode(bytes)
                ))
                .toList();
    }
}

