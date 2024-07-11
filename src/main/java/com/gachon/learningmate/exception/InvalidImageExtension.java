package com.gachon.learningmate.exception;

public class InvalidImageExtension extends RuntimeException {
    public InvalidImageExtension(String message) {
        super(message);
    }
}
