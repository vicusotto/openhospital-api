package org.isf.shared.controller;


import org.isf.shared.controller.dto.*;

import java.util.Map;

public final class OHResponses {

    private OHResponses() {
    }

    public static OHSuccessDTO success() {
        return new OHSuccessDTO();
    }

    public static <T> OHSuccessWrapperDTO<T> success(T response) {
        return new OHSuccessWrapperDTO<>(response);
    }

    public static OHFailureDTO failure(OHResponseCode responseCode, String message) {
        return new OHFailureDTO(responseCode, message);
    }

    public static OHValidationErrorDTO failure(String message, Map<String, Object> validationErrors, OHResponseCode responseCode) {
        return new OHValidationErrorDTO(message, validationErrors, responseCode);
    }
}
