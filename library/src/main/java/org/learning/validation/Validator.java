package org.learning.validation;

import org.learning.dto.DataObject;
import org.learning.exception.InvalidPayloadException;
import org.learning.exception.UnHandledException;

public abstract class Validator {

    protected DataObject dataObject;

    Validator(DataObject dataObject) {
        this.dataObject = dataObject;
    }


    public abstract String getErrorResponse();

    protected void validateStatusCode() throws UnHandledException, InvalidPayloadException {
        var statusCode = dataObject.getStatusCode();
        switch (statusCode) {
            case 400:
                throw new InvalidPayloadException(getErrorResponse());
            case 500:
                throw new UnHandledException(getErrorResponse());
        }
    }

    public abstract void validateResponseHeaders();

    public abstract void validateResponseBody();
}
