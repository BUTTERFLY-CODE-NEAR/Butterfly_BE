package com.codenear.butterfly.certify.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class CertifyException extends BusinessBaseException {

    public CertifyException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
