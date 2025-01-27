package com.codenear.butterfly.certify.application;

import com.codenear.butterfly.mail.application.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class MailServiceTest {
    @Autowired
    private MailService mailService;

    @Test
    void testSendCertifyCode() {
        // Given
        String testEmail = "9659tig@naver.com"; // 실제 테스트할 이메일 주소

        try {
            // When
            String code = mailService.sendCertifyCode(testEmail);

            // Then
            assertNotNull(code); // 인증 코드가 null이 아닌지 확인
            assertEquals(6, code.length()); // 인증 코드의 길이가 6인지 확인
            System.out.println("발송된 인증 코드: " + code);

        } catch (Exception e) {
            e.printStackTrace();
            fail("메일 발송 중 오류 발생: " + e.getMessage());
        }
    }
}