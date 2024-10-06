package com.codenear.butterfly.kakaoPay.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class KakaoPayException extends BusinessBaseException {
    public KakaoPayException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
