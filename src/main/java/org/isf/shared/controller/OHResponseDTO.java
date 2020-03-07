package org.isf.shared.controller;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OHResponseDTO implements Serializable {

    private OHResponseType responseType;
    private OHResponseCode responseCode;
    private String message;
    private Map<String, Object> validationErrors;

    private Object response;

    public OHResponseDTO(OHResponseType responseType, OHResponseCode responseCode) {
        this.responseType = responseType;
        this.responseCode = responseCode;
    }

    public OHResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(OHResponseType responseType) {
        this.responseType = responseType;
    }

    public OHResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(OHResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, Object> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
