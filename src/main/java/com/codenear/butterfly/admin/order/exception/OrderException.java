package com.codenear.butterfly.admin.order.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class OrderException extends BusinessBaseException {

    public OrderException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
