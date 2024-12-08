package com.codenear.butterfly.promotion.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class PromotionException extends BusinessBaseException {

    public PromotionException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
