package com.example.repository;

public class DataAccessCustomException extends RuntimeException {
    public DataAccessCustomException(String message) {
        super(message);
    }
    
    public DataAccessCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
