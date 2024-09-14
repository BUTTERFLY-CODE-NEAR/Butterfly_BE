package com.codenear.butterfly.member.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class MemberException extends BusinessBaseException {

    public MemberException(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
