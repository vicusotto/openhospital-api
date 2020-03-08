package org.isf.shared.controller.dto;

import java.util.Map;

public class OHValidationErrorDTO extends OHFailureDTO {

    private static final long serialVersionUID = 8389822547860507326L;

    private Map<String, Object> validationErrors;

    public OHValidationErrorDTO(String message, Map<String, Object> validationErrors, OHResponseCode responseCode) {
        super(responseCode, message);
        this.validationErrors = validationErrors;
    }

    public Map<String, Object> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, Object> validationErrors) {
        this.validationErrors = validationErrors;
    }

}
