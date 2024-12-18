package com.codenear.butterfly.notify.fcm.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.application.AlarmService;
import com.codenear.butterfly.notify.fcm.infrastructure.FCMRepository;
import com.codenear.butterfly.notify.fcm.infrastructure.FirebaseMessagingClient;
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
    public void send(NotifyMessage message, Long memberId) {
        if (!checkConsent(message, memberId)) {
            return;
        }

        fcmRepository.findByMemberId(memberId)
                .stream()
                .map(fcm -> createMessage(fcmMessageConstant, fcm.getToken()))
                .forEach(firebaseMessagingClient::sendMessage);
    }

    @Transactional
    public void sendTopic(NotifyMessage message, String topic) {
        Message topicMessage = createTopicMessage(message, topic);
        firebaseMessagingClient.sendMessage(topicMessage);
    }

    private boolean checkConsent(NotifyMessage message, Long memberId) {
        List<Consent> consents = consentFacade.getConsents(memberId);

        Consent first = consents.stream()
                .filter(consent -> consent.getConsentType().equals(message.getConsentType()))
                .findFirst()
                .orElse(null);

        return first != null && first.isAgreed();
    }

    private Message createMessage(NotifyMessage message, String token) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(message.getSubtitle())
                        .setBody(message.getContent())
                        .build())
                .setToken(token)
                .build();
    }

    private Message createTopicMessage(NotifyMessage message, String topic) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(message.getSubtitle())
                        .setBody(message.getContent())
                        .build())
                .setTopic(topic)
                .build();
    }
}
