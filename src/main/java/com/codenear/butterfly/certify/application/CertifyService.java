package com.codenear.butterfly.certify.application;

import com.codenear.butterfly.certify.domain.CertifyType;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.certify.exception.CertifyException;
import com.codenear.butterfly.global.util.RandomUtil;
import com.codenear.butterfly.mail.application.MailService;
import com.codenear.butterfly.sms.application.SmsService;
import java.util.concurrent.TimeUnit;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.codenear.butterfly.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CertifyService {

    private static final String MESSAGE = "나비 본인 확인 인증번호 [%s]을(를) 입력해 주세요.";
    private static final int CERTIFY_CODE_EXPIRE_MINUTES = 5;
    private static final int CERTIFY_CODE_LENGTH = 6;

    private final SmsService smsService;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendCertifyCode(String identifier, CertifyType certifyType) {
        String code = generateCertifyCode();

        switch (certifyType) {
            case CERTIFY_PHONE -> {
                String message = String.format(MESSAGE, code);

                smsService.sendSMS(identifier, message);
            }
            case CERTIFY_EMAIL -> {
                try {
                    mailService.sendCertifyCode(identifier, code);
                } catch (MessagingException e) {
                    throw new CertifyException(VALIDATION_FAILED, e);
                }
            }
        }

        String key = createRedisKey(identifier, certifyType);
        storeCertifyCode(key, code);
    }

    public void checkCertifyCode(CertifyRequest request, CertifyType certifyType) {
        String key = switch (certifyType) {
            case CERTIFY_PHONE -> createRedisKey(request.phoneNumber(), certifyType);
            case CERTIFY_EMAIL -> createRedisKey(request.email(), certifyType);
        };

        String storedCode = getStoredCode(key);

        validateCertifyCode(storedCode, request.certifyCode());

        deleteStoredCode(key);
    }

    private String generateCertifyCode() {
        return String.valueOf(RandomUtil.generateRandomNum(CERTIFY_CODE_LENGTH));
    }

    private String createRedisKey(String phoneNumber, CertifyType certifyType) {
        return certifyType.getRedisKey(phoneNumber);
    }

    private void storeCertifyCode(String key, String code) {
        redisTemplate.opsForValue()
                .set(key, code, CERTIFY_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    private String getStoredCode(String key) {
        return redisTemplate.opsForValue()
                .get(key);
    }

    private void validateCertifyCode(String storedCode, String inputCode) {
        if (storedCode == null)
            throw new CertifyException(CERTIFY_CODE_EXPIRED, null);

        if (!storedCode.equals(inputCode))
            throw new CertifyException(CERTIFY_CODE_MISMATCH, null);
    }

    private void deleteStoredCode(String key) {
        redisTemplate.delete(key);
    }
}
