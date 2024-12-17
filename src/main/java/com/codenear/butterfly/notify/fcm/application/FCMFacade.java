package com.codenear.butterfly.notify.fcm.application;

import com.codenear.butterfly.notify.domain.NotifyMessage;
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

    public void sendMessage(NotifyMessage fcmMessageConstant, Long memberId) {
        fcmMessageService.send(fcmMessageConstant, memberId);
    }

    public void sendTopicMessage(NotifyMessage fcmMessageConstant, String topic) {
        fcmMessageService.sendTopic(fcmMessageConstant, topic);
    }

    public void subscribeToTopic(Long memberId, String topic) {
        fcmTokenService.subscribeToTopic(memberId, topic);
    }

    public void unsubscribeFromTopic(Long memberId, String topic) {
        fcmTokenService.unsubscribeFromTopic(memberId, topic);
    }
}
