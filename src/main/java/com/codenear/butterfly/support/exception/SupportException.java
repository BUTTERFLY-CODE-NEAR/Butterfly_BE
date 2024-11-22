package com.codenear.butterfly.support.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class SupportException extends BusinessBaseException {

    public SupportException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
