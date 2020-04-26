package org.isf.shared.exceptions;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHNotFoundApiException extends OHAPIException {
    public OHNotFoundApiException(OHExceptionMessage message) {
        super(message);
    }
}
