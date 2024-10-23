package com.codenear.butterfly.certify.application;

import com.codenear.butterfly.certify.dto.CertifyRequestDTO;
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
        String randomNum = String.valueOf(RandomUtil.generateRandomNum(CERTIFY_CODE_LENGTH));
        smsService.sendSMS(phoneNumber, randomNum); // todo : 문자 내용 받으면, 수정해야함
        redisTemplate.opsForValue().set(phoneNumber, randomNum, CERTIFY_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public void checkCertifyCode(CertifyRequestDTO requestDTO, MemberDTO memberDTO) {
        String storedCode = redisTemplate.opsForValue().get(requestDTO.getPhoneNumber());
        certifyValidator.validateCertifyCode(storedCode, requestDTO.getCertifyCode());
        memberService.updatePhoneNumber(memberDTO.getId(), requestDTO.getPhoneNumber());
    }
}
