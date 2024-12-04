package com.codenear.butterfly.certify.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;
import static com.codenear.butterfly.global.exception.ErrorCode.VALIDATION_FAILED_CODE_MISMATCH;

import com.codenear.butterfly.certify.exception.CertifyException;

public class CertifyValidator {

    public static void validateCertifyCode(String storedCode, String inputCode) {
        if (storedCode == null)
            throw new CertifyException(SERVER_ERROR, null);

        if (!storedCode.equals(inputCode))
            throw new CertifyException(VALIDATION_FAILED_CODE_MISMATCH, null);
    }
}
