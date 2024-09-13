package com.codenear.butterfly.global.exception;

import lombok.Getter;

@Getter
public class BusinessBaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object body;

    public BusinessBaseException(ErrorCode errorCode, Object body) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.body = body;
    }
}
