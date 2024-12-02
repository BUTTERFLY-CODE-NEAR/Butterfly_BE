package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FCMFacade {

    private final FCMTokenService fcmTokenService;
    private final FCMMessageService fcmMessageService;

    public void save(String token, MemberDTO loginMember) {
        fcmTokenService.saveFCM(token, loginMember);
    }

    public void sendMessage(String title, String body, Long memberId) {
        fcmMessageService.send(title, body, memberId);
    }

    public void sendTopicMessage(String title, String body, String topic) {
        fcmMessageService.sendTopic(title, body, topic);
    }
}
