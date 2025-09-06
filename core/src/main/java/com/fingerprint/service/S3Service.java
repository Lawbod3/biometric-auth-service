package com.fingerprint.service;

import com.fingerprint.dto.response.PreSignedResponse;

import java.util.Optional;


public interface S3Service {
  Optional<PreSignedResponse> generatePreSignedUrl(String userId, String finger);

  void deleteObjectFromS3(String s3Key);
}
