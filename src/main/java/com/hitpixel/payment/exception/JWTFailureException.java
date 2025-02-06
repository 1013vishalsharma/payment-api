package com.hitpixel.payment.exception;

public class JWTFailureException extends RuntimeException {
    public JWTFailureException(String message) {
        super(message);
    }
}
