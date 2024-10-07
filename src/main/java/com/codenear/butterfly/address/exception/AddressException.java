package com.codenear.butterfly.address.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class AddressException extends BusinessBaseException {

    public AddressException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
