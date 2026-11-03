package com.hospital.treatment.exception;

/** Thrown when a record has a NULL/empty Patient ID, Test or Status, or is malformed. */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) { super(message); }
}
