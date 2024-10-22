package com.codenear.butterfly.sms.application;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface SmsService {
    SingleMessageSentResponse sendSMS(String phoneNumber, String message);
}
