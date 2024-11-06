package com.codenear.butterfly.geocoding.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class GeocodingException extends BusinessBaseException {

    public GeocodingException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
