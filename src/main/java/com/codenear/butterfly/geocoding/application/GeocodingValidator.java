package com.codenear.butterfly.geocoding.application;

import static com.codenear.butterfly.geocoding.exception.ErrorMessage.RESPONSE_BODY_DATA_NULL;
import static com.codenear.butterfly.geocoding.exception.ErrorMessage.RESPONSE_BODY_NULL;
import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.geocoding.domain.dto.GeocodingResponse;
import com.codenear.butterfly.geocoding.exception.GeocodingException;
import org.springframework.stereotype.Component;

@Component
public class GeocodingValidator {

    private static final String SUCCESS_STATUS = "OK";

    public void validateResponse(GeocodingResponse response) {
        validateMetaNotNull(response);
        validateStatus(response);
        validateTotalCount(response);
    }

    private void validateMetaNotNull(GeocodingResponse response) {
        if (response.getMeta() == null) {
            throw new GeocodingException(SERVER_ERROR, RESPONSE_BODY_NULL);
        }
    }

    private void validateStatus(GeocodingResponse response) {
        if (!response.getStatus().equals(SUCCESS_STATUS)) {
            String errorMessage = response.getErrorMessage();
            throw new GeocodingException(SERVER_ERROR, errorMessage);
        }
    }

    private void validateTotalCount(GeocodingResponse response) {
        if (response.getMeta().getTotalCount() == 0) {
            throw new GeocodingException(SERVER_ERROR, RESPONSE_BODY_DATA_NULL);
        }
    }
}
