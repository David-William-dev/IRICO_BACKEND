package com.irico.backend.source.exception;

public class SourceAlreadyExistsException extends RuntimeException {
    public SourceAlreadyExistsException(String message) {
        super(message);
    }
}