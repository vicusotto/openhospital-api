package org.isf.shared.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class OHResponse extends ResponseEntity<OHResponseDTO> {

    public OHResponse(HttpStatus status) {
        super(status);
    }

    public OHResponse(OHResponseDTO body, HttpStatus status) {
        super(body, status);
    }

    public OHResponse(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, status);
    }

    public OHResponse(OHResponseDTO body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
    }

    public static OHResponse success(HttpStatus httpStatus) {
        return new OHResponse(new OHResponseDTO(OHResponseType.SUCCESS, OHResponseCode.SUCCESS), httpStatus);
    }

    public static OHResponse info() {
        return new OHResponse(new OHResponseDTO(OHResponseType.INFO, OHResponseCode.SUCCESS), HttpStatus.OK);
    }

    public static OHResponse warning() {
        return new OHResponse(new OHResponseDTO(OHResponseType.WARNING, OHResponseCode.SUCCESS), HttpStatus.OK);
    }

    public static OHResponse error(OHResponseCode responseCode, HttpStatus httpStatus) {
        return new OHResponse(new OHResponseDTO(OHResponseType.ERROR, responseCode), httpStatus);
    }

    public <T> OHResponse withData(T data) {
        ((OHResponseDTO) this.getBody()).setResponse(data);
        return this;
    }

    public OHResponse withMesage(String message) {
        ((OHResponseDTO) this.getBody()).setMessage(message);
        return this;
    }

    public OHResponse withValidationErrors(Map<String, Object> validationErrors) {
        ((OHResponseDTO) this.getBody()).setValidationErrors(validationErrors);
        return this;
    }

}
