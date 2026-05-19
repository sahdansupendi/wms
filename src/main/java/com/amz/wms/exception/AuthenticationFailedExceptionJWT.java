package com.amz.wms.exception;

public class AuthenticationFailedExceptionJWT extends RuntimeException {
    public AuthenticationFailedExceptionJWT(String message) {
        super(message);
    }
}
