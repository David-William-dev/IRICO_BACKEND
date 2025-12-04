package com.irico.backend.handler;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String code;
    private Object message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String code, Object message, LocalDateTime timestamp) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }
}