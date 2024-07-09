package com.gachon.learningmate.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
