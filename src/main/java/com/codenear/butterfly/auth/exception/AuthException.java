package com.codenear.butterfly.auth.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class AuthException extends BusinessBaseException {

    public AuthException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
