package com.fingerprint.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreSignedResponse {
    private List<String> preSignedUrls;
}
