package com.codenear.butterfly.sms.application;

import com.codenear.butterfly.sms.domain.dto.CloudSmsResponseDTO;

public interface SmsService {
    CloudSmsResponseDTO sendSMS(String phoneNumber, String message);
}
