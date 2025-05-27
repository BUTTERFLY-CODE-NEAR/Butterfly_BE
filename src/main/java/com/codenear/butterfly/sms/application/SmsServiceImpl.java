package com.codenear.butterfly.sms.application;

import com.codenear.butterfly.sms.config.SmsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsConfig smsConfig;

    @Override
    public void sendSMS(String phoneNumber, String message) {
        smsConfig.sendSms(phoneNumber, message);
    }
}
