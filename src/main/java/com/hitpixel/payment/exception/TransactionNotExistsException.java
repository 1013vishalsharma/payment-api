package com.hitpixel.payment.exception;

public class TransactionNotExistsException extends RuntimeException {
    public TransactionNotExistsException(String message) {
        super(message);
    }
}
