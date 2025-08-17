package org.learning.exception;

import org.apache.http.HttpStatus;

public class UnHandledException extends OutboundCallException {

    public UnHandledException(String errorMessage) {
        super(HttpStatus.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
}
