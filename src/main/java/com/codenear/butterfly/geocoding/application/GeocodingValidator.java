package com.codenear.butterfly.geocoding.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.geocoding.domain.dto.GeocodingResponse;
import com.codenear.butterfly.geocoding.exception.ErrorMessage;
import com.codenear.butterfly.geocoding.exception.GeocodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GeocodingValidator {

    public void validateResponse(ResponseEntity<GeocodingResponse> response) {
        validateResponseBody(response);
        validateResponseStatus(response);
        validateResponseTotalCount(response);
    }

    private void validateResponseBody(ResponseEntity<GeocodingResponse> response) {
        if (response.getBody() == null) {
            throw new GeocodingException(SERVER_ERROR, ErrorMessage.RESPONSE_BODY_NULL);
        }
    }

    private void validateResponseStatus(ResponseEntity<GeocodingResponse> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            String errorMessage = response.getBody().getErrorMessage();
            throw new GeocodingException(SERVER_ERROR, errorMessage);
        }
    }

    private void validateResponseTotalCount(ResponseEntity<GeocodingResponse> response) {
        if (response.getBody().getMeta().getTotalCount() == 0) {
            throw new GeocodingException(SERVER_ERROR, ErrorMessage.RESPONSE_TOTAL_COUNT_ZERO);
        }
    }
}
