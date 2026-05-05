package com.example.wms.exception;

public class AuthenticationFailedExceptionJWT extends RuntimeException {
    public AuthenticationFailedExceptionJWT(String message) {
        super(message);
    }
}
