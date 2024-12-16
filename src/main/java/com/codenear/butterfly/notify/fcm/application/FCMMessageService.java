package com.codenear.butterfly.notify.fcm.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.notify.fcm.domain.FCMMessageConstant;
import com.codenear.butterfly.notify.fcm.domain.FCMRepository;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMMessageService {

    private final FCMRepository fcmRepository;
    private final FirebaseMessagingClient firebaseMessagingClient;
    private final ConsentFacade consentFacade;

    @Transactional
    protected void send(FCMMessageConstant fcmMessageConstant, Long memberId) {
        if (!checkConsent(fcmMessageConstant, memberId)) {
            return;
        }

        fcmRepository.findByMemberId(memberId)
                .stream()
                .map(fcm -> createMessage(fcmMessageConstant, fcm.getToken()))
                .forEach(firebaseMessagingClient::sendMessage);
    }

    @Transactional
    protected void sendTopic(FCMMessageConstant fcmMessageConstant, String topic) {
        Message topicMessage = createTopicMessage(fcmMessageConstant, topic);
        firebaseMessagingClient.sendMessage(topicMessage);
    }

    private boolean checkConsent(FCMMessageConstant fcmMessageConstant, Long memberId) {
        List<Consent> consents = consentFacade.getConsents(memberId);

        Consent first = consents.stream()
                .filter(consent -> consent.getConsentType().equals(fcmMessageConstant.getConsentType()))
                .findFirst()
                .orElse(null);

        return first != null && first.isAgreed();
    }

    private Message createMessage(FCMMessageConstant message, String token) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(message.getTitle())
                        .setBody(message.getBody())
                        .build())
                .setToken(token)
                .build();
    }

    private Message createTopicMessage(FCMMessageConstant message, String topic) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(message.getTitle())
                        .setBody(message.getBody())
                        .build())
                .setTopic(topic)
                .build();
    }
}
