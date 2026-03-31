package com.app.quantitymeasurementapp.exception;

public class DatabaseException extends QuantityMeasurementException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}