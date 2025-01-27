package com.codenear.butterfly.mail.application;

import com.codenear.butterfly.global.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private static final int CERTIFY_CODE_LENGTH = 6;
    @Value("${mail.username}")
    private String senderEmail;

    public String sendCertifyCode(String sendEmail) throws MessagingException {
        String code = generateCertifyCode();

        MimeMessage message = createMail(sendEmail, code);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
        }

        return code;
    }

    private MimeMessage createMail(String mail, String code) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>나비 본인 확인 인증번호를 입력해 주세요..</h3>";
        body += "<h1>" + code + "</h1>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    private String generateCertifyCode() {
        return String.valueOf(RandomUtil.generateRandomNum(CERTIFY_CODE_LENGTH));
    }
}
