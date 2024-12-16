package com.codenear.butterfly.notify.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class NotifyException extends BusinessBaseException {

    public NotifyException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
