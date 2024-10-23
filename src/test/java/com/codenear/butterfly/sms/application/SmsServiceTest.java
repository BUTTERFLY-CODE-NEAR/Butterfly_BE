package com.codenear.butterfly.sms.application;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmsServiceTest {

    @Autowired
    private SmsService smsService;

    @Value("${cool.sms.test}")
    private String fromPhoneNumber;

    @Test
    @Disabled // 포인트 차감으로, 최종 테스트만 사용 권장
    void 문자_전송_테스트() {
        // given
        String testMessage = "Test Message";

        // when
        SingleMessageSentResponse response = smsService.sendSMS(fromPhoneNumber, testMessage);

        // then
        Assertions.assertEquals(response.getStatusCode(), "2000"); // coolSms 성공 상태 코드가 "2000"
    }
}