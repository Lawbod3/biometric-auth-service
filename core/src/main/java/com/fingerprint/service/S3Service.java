package com.fingerprint.service;

import com.fingerprint.dto.request.PreSignedUrlRequest;
import com.fingerprint.dto.response.PreSignedResponse;


public interface S3Service {
   PreSignedResponse getPreSignedUrl(PreSignedUrlRequest preSignedUrlRequest);
}
