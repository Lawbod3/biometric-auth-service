package com.fingerprint.dto.response;




public class ApiResponse {
    private boolean success;
    private Object data;

    public ApiResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }
}
