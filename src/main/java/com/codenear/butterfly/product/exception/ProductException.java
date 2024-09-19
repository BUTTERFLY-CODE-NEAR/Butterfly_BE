package com.codenear.butterfly.product.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class ProductException extends BusinessBaseException {

    public ProductException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
