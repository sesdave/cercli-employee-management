package com.cercli.employee.exception;

/**
 * Custom exception thrown when an email address already exists in the system.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
