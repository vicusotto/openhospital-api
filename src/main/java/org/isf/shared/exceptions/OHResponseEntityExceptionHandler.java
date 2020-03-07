package org.isf.shared.exceptions;

import org.isf.shared.controller.OHResponse;
import org.isf.shared.controller.OHResponseCode;
import org.isf.shared.controller.OHResponseDTO;
import org.isf.shared.controller.OHResponseType;
import org.isf.utils.exception.*;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class OHResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = {OHDataValidationException.class})
    protected OHResponse handleOHServiceValidationException(OHDataValidationException ex, Locale locale) {
        final Map<String, Object> errors = ex.getMessages().stream().collect(Collectors.toMap(OHExceptionMessage::getMessage, e -> messageSource.getMessage(e.getMessage(), null, locale)));
        return OHResponse.error(OHResponseCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST).withValidationErrors(errors);
    }

    @ExceptionHandler(value = {OHReportException.class})
    protected OHResponse handleReportException(OHReportException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.REPORT_ERROR.getValue());
        return OHResponse.error(OHResponseCode.REPORT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(message);
    }

    @ExceptionHandler(value = {OHInvalidSQLException.class})
    protected OHResponse handleInvalidSqlException(OHInvalidSQLException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.INVALID_SQL_ERROR.getValue());
        return OHResponse.error(OHResponseCode.INVALID_SQL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(message);
    }

    @ExceptionHandler(value = {OHOperationNotAllowedException.class})
    protected OHResponse handleOHServiceOperationNotAllowedException(OHOperationNotAllowedException ex, Locale locale) {
        final Map<String, Object> errors = ex.getMessages().stream().collect(Collectors.toMap(OHExceptionMessage::getMessage, e -> messageSource.getMessage(e.getMessage(), null, locale)));
        return OHResponse.error(OHResponseCode.OPERATION_NOT_ALLOWED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withValidationErrors(errors);
    }

    @ExceptionHandler(value = {OHDicomException.class})
    protected OHResponse handleDicomException(OHDicomException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DICOM_ERROR.getValue());
        return OHResponse.error(OHResponseCode.DICOM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(message);
    }

    @ExceptionHandler(value = {OHDBConnectionException.class})
    protected OHResponse handleDbConnectionException(OHDBConnectionException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DATABASE_CONNECTION_ERROR.getValue());
        return OHResponse.error(OHResponseCode.DATABASE_CONNECTION_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(message);
    }

    @ExceptionHandler(value = {OHDataIntegrityViolationException.class})
    protected OHResponse handleDataIntegrityViolationException(OHDataIntegrityViolationException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DATA_INTEGRITY_ERROR.getValue());
        return OHResponse.error(OHResponseCode.DATA_INTEGRITY_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(message);
    }

    @ExceptionHandler(value = {OHDataLockFailureException.class})
    protected OHResponse handleDbLockFailureException(OHDataLockFailureException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DATA_LOCK_ERROR.getValue());
        return OHResponse.error(OHResponseCode.DATA_INTEGRITY_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(message);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Map<String, Object> errors = new HashMap<>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }
        final OHResponseDTO responseDTO = new OHResponseDTO(OHResponseType.ERROR, OHResponseCode.INVALID_PARAMETER);
        responseDTO.setValidationErrors(errors);

        return handleExceptionInternal(ex, responseDTO, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        OHResponseDTO responseDTO = new OHResponseDTO(OHResponseType.ERROR, OHResponseCode.INVALID_PARAMETER);
        responseDTO.setMessage(OHResponseCode.INVALID_PARAMETER.getValue());
        return handleExceptionInternal(ex, responseDTO, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {OHServiceException.class})
    protected OHResponse handleOHServiceException(OHServiceException ex) {
        return OHResponse.error(OHResponseCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    protected OHResponse handleGenericException(Exception ex) {
        return OHResponse.error(OHResponseCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {OHAPIException.class})
    protected OHResponse handleOHAPIException(OHAPIException ex) {
        return OHResponse.error(OHResponseCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR).withMesage(ex.getMessage());
    }


}
