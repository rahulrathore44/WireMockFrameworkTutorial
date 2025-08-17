package org.learning.exception;

public abstract class OutboundCallException extends Exception {

    private final int statusCode;

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    private final String errorMessage;

    public OutboundCallException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
