package org.isf.shared.exceptions;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHCreateAPIException extends OHAPIException {
    public OHCreateAPIException(OHExceptionMessage message) {
        super(message);
    }
}
