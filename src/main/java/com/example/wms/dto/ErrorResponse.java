package com.example.wms.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, Object> details
) {

    public ErrorResponse (int status, String error, String message, String path) {
        this(status,error,message,LocalDateTime.now(), null);
    }

    public ErrorResponse withDetails(Map<String, Object> details) {
        return new ErrorResponse(status,error,message,timestamp,details);
    }

}
