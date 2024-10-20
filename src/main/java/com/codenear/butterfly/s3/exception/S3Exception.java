package com.codenear.butterfly.s3.exception;

import com.codenear.butterfly.global.exception.BusinessBaseException;
import com.codenear.butterfly.global.exception.ErrorCode;

public class S3Exception extends BusinessBaseException {

    public S3Exception(ErrorCode errorCode, Object body) {
        super(errorCode, body);
    }
}
