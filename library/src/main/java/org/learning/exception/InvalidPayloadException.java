package org.learning.exception;

import org.apache.http.HttpStatus;

public class InvalidPayloadException extends OutboundCallException {

    public InvalidPayloadException(String errorMessage) {
        super(HttpStatus.SC_BAD_REQUEST, errorMessage);
    }
}
