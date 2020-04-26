package org.isf.shared.exceptions;

import org.isf.shared.controller.OHResponses;
import org.isf.shared.controller.dto.OHFailureDTO;
import org.isf.shared.controller.dto.OHResponseCode;
import org.isf.shared.controller.dto.OHValidationErrorDTO;
import org.isf.utils.exception.*;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected OHValidationErrorDTO handleOHServiceValidationException(OHDataValidationException ex, Locale locale) {
        final Map<String, Object> errors = ex.getMessages().stream().collect(Collectors.toMap(OHExceptionMessage::getMessage, e -> messageSource.getMessage(e.getMessage(), null, locale)));
        return OHResponses.failure(OHResponseCode.VALIDATION_ERROR.getValue(), errors, OHResponseCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(value = {OHReportException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleReportException(OHReportException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.REPORT_ERROR.getValue());
        return OHResponses.failure(OHResponseCode.REPORT_ERROR, message);
    }

    @ExceptionHandler(value = {OHInvalidSQLException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleInvalidSqlException(OHInvalidSQLException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.INVALID_SQL_ERROR.getValue());
        return OHResponses.failure(OHResponseCode.INVALID_SQL_ERROR, message);
    }

    @ExceptionHandler(value = {OHOperationNotAllowedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHValidationErrorDTO handleOHServiceOperationNotAllowedException(OHOperationNotAllowedException ex, Locale locale) {
        final Map<String, Object> errors = ex.getMessages().stream().collect(Collectors.toMap(OHExceptionMessage::getMessage, e -> messageSource.getMessage(e.getMessage(), null, locale)));
        return OHResponses.failure(OHResponseCode.VALIDATION_ERROR.getValue(), errors, OHResponseCode.OPERATION_NOT_ALLOWED_ERROR);
    }

    @ExceptionHandler(value = {OHDicomException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleDicomException(OHDicomException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DICOM_ERROR.getValue());
        return OHResponses.failure(OHResponseCode.DICOM_ERROR, message);
    }

    @ExceptionHandler(value = {OHDBConnectionException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleDbConnectionException(OHDBConnectionException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DATABASE_CONNECTION_ERROR.getValue());
        return OHResponses.failure(OHResponseCode.DATABASE_CONNECTION_ERROR, message);
    }

    @ExceptionHandler(value = {OHDataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleDataIntegrityViolationException(OHDataIntegrityViolationException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DATA_INTEGRITY_ERROR.getValue());
        return OHResponses.failure(OHResponseCode.DATA_INTEGRITY_ERROR, message);
    }

    @ExceptionHandler(value = {OHDataLockFailureException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleDbLockFailureException(OHDataLockFailureException ex, Locale locale) {
        String message = ex.getMessages().stream().map(m -> messageSource.getMessage(m.getMessage(), null, locale)).findFirst().orElse(OHResponseCode.DATA_LOCK_ERROR.getValue());
        return OHResponses.failure(OHResponseCode.DATA_LOCK_ERROR, message);
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
        final OHValidationErrorDTO validationErrorDTO = new OHValidationErrorDTO(OHResponseCode.INVALID_PARAMETER.getValue(), errors, OHResponseCode.INVALID_PARAMETER);

        return handleExceptionInternal(ex, validationErrorDTO, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {OHServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleOHServiceException(OHServiceException ex) {
        return OHResponses.failure(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
    }

    @ExceptionHandler(value = {OHNotFoundApiException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected OHFailureDTO handleOHNotFoundApiException(OHNotFoundApiException ex) {
        return OHResponses.failure(OHResponseCode.NOT_FOUND, ex.getMessages().get(0).getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleGenericException(Exception ex) {
        return OHResponses.failure(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
    }

    @ExceptionHandler(value = {OHAPIException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected OHFailureDTO handleOHAPIException(OHAPIException ex) {
        return OHResponses.failure(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
    }


}
