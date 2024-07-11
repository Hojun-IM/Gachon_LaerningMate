package com.gachon.learningmate.exception;

public class FileDeleteFailureException extends RuntimeException {
    public FileDeleteFailureException(String message) {
        super(message);
    }
}
