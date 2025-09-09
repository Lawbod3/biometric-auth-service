package com.fingerprint.service;

import com.machinezoo.sourceafis.FingerprintTemplate;

import java.util.List;

public interface TemplateExtractor {
    List<FingerprintTemplate> extractTemplates(List<byte[]> imageBytes);
}
