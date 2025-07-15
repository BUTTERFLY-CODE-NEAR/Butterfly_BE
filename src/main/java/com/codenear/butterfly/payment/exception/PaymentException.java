package com.codenear.butterfly.payment.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class PaymentException extends BusinessBaseException {
    public PaymentException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
