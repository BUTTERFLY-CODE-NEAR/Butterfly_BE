package com.codenear.butterfly.certify.application;

import static com.codenear.butterfly.certify.domain.CertifyMessage.*;

import com.codenear.butterfly.certify.domain.dto.CertifyRequestDTO;
import com.codenear.butterfly.global.util.RandomUtil;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.sms.application.SmsService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertifyService {
    public static final int CERTIFY_CODE_EXPIRE_MINUTES = 5;
    public static final int CERTIFY_CODE_LENGTH = 6;

    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;
    private final CertifyValidator certifyValidator;
    private final MemberService memberService;

    public void sendCertifyCode(String phoneNumber) {
        String certifyCode = generateCertifyCode();
        String certifyMessage = SMS.getMessage(certifyCode);

        smsService.sendSMS(phoneNumber, certifyMessage);
        redisTemplate.opsForValue().set(phoneNumber, certifyCode, CERTIFY_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public void checkCertifyCode(CertifyRequestDTO requestDTO, MemberDTO memberDTO) {
        String storedCode = redisTemplate.opsForValue().get(requestDTO.getPhoneNumber());
        certifyValidator.validateCertifyCode(storedCode, requestDTO.getCertifyCode());
        memberService.updatePhoneNumber(memberDTO.getId(), requestDTO.getPhoneNumber());
    }

    private String generateCertifyCode() {
        return String.valueOf(RandomUtil.generateRandomNum(CERTIFY_CODE_LENGTH));
    }
}
