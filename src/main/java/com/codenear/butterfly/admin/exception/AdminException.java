package com.codenear.butterfly.admin.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class AdminException extends BusinessBaseException {

    public AdminException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
