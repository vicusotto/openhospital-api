package org.isf.shared.exceptions;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHUpdateAPIException extends OHAPIException {
    public OHUpdateAPIException(OHExceptionMessage message) {
        super(message);
    }
}
