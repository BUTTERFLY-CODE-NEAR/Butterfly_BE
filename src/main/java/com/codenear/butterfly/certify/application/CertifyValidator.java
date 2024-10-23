package com.codenear.butterfly.certify.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.exception.MemberException;
import org.springframework.stereotype.Component;

@Component
public class CertifyValidator {

    public void validateCertifyCode(String storedCode, String inputCode) {
        if (storedCode == null)
            throw new MemberException(ErrorCode.SERVER_ERROR, null);

        if (!storedCode.equals(inputCode))
            throw new MemberException(ErrorCode.VALIDATION_FAILED_CODE_MISMATCH, null);
    }
}
