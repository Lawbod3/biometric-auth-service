package com.fingerprint.service;

import com.fingerprint.dto.request.PreSignedUrlRequest;
import com.fingerprint.dto.response.PreSignedResponse;

import java.util.Optional;


public interface S3Service {
  Optional<PreSignedResponse> getPreSignedUrl(PreSignedUrlRequest preSignedUrlRequest);
}
