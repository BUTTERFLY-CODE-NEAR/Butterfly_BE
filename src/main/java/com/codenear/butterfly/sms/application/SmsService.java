package com.codenear.butterfly.sms.application;

public interface SmsService {
    void sendSMS(String phoneNumber, String message);
}
