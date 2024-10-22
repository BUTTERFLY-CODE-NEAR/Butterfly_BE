package com.codenear.butterfly.sms.application;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final DefaultMessageService messageService;

    @Value("${cool.sms.from}")
    private String fromPhoneNumber;

    @Override
    public SingleMessageSentResponse sendSMS(String phoneNumber, String message) {
        Message coolSms = createMessageInfo(phoneNumber, message);
        return messageService.sendOne(new SingleMessageSendingRequest(coolSms));
    }

    private Message createMessageInfo(String phoneNumber, String message) {
        Message coolSms = new Message();

        coolSms.setFrom(fromPhoneNumber);
        coolSms.setTo(phoneNumber);
        coolSms.setText(message);

        return coolSms;
    }
}
