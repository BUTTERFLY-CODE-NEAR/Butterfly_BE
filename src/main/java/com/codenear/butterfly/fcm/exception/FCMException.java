package com.codenear.butterfly.fcm.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class FCMException extends BusinessBaseException {

    public FCMException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
