package com.codenear.butterfly.sms.application;

import com.codenear.butterfly.sms.config.SmsConfig;
import com.codenear.butterfly.sms.domain.dto.CloudSmsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsConfig smsConfig;

    @Override
    public CloudSmsResponseDTO sendSMS(String phoneNumber, String message) {
        return smsConfig.sendSms(phoneNumber, message);
    }
}
