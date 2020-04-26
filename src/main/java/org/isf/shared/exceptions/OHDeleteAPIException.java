package org.isf.shared.exceptions;

import org.isf.utils.exception.model.OHExceptionMessage;

public class OHDeleteAPIException extends OHAPIException {
    public OHDeleteAPIException(OHExceptionMessage message) {
        super(message);
    }
}
