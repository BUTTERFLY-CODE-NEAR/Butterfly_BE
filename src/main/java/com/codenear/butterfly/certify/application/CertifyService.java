package com.codenear.butterfly.certify.application;

import static com.codenear.butterfly.global.exception.ErrorCode.CERTIFY_CODE_EXPIRED;
import static com.codenear.butterfly.global.exception.ErrorCode.CERTIFY_CODE_MISMATCH;

import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.certify.exception.CertifyException;
import com.codenear.butterfly.global.util.RandomUtil;
import com.codenear.butterfly.sms.application.SmsService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertifyService {

    private static final String MESSAGE = "나비 본인 확인 인증번호 [%s]을(를) 입력해 주세요.";
    private static final int CERTIFY_CODE_EXPIRE_MINUTES = 5;
    private static final int CERTIFY_CODE_LENGTH = 6;

    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendCertifyCode(String phoneNumber) {
        String code = generateCertifyCode();
        String message = String.format(MESSAGE, code);

        smsService.sendSMS(phoneNumber, message);
        storeCertifyCode(phoneNumber, code);
    }

    public void checkCertifyCode(CertifyRequest request) {
        String storedCode = getStoredCode(request.phoneNumber());
        validateCertifyCode(storedCode, request.certifyCode());
    }

    private String generateCertifyCode() {
        return String.valueOf(RandomUtil.generateRandomNum(CERTIFY_CODE_LENGTH));
    }

    private void validateCertifyCode(String storedCode, String inputCode) {
        if (storedCode == null)
            throw new CertifyException(CERTIFY_CODE_EXPIRED, null);

        if (!storedCode.equals(inputCode))
            throw new CertifyException(CERTIFY_CODE_MISMATCH, null);
    }

    private void storeCertifyCode(String phoneNumber, String code) {
        redisTemplate.opsForValue().set(phoneNumber, code, CERTIFY_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    private String getStoredCode(String phoneNumber) {
        return redisTemplate.opsForValue().get(phoneNumber);
    }
}
