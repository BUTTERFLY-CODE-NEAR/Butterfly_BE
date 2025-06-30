package com.codenear.butterfly.notify.fcm.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.application.AlarmService;
import com.codenear.butterfly.notify.fcm.domain.FCM;
import com.codenear.butterfly.notify.fcm.infrastructure.FCMRepository;
import com.codenear.butterfly.notify.fcm.infrastructure.FirebaseMessagingClient;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMMessageService {

    private final FCMRepository fcmRepository;
    private final FirebaseMessagingClient firebaseMessagingClient;
    private final ConsentFacade consentFacade;
    private final AlarmService alarmService;

    @Transactional
    public void sendNotificationMessage(NotifyMessage message, Long memberId) {
        if (!isConsentGiven(message, memberId)) {
            return;
        }
        List<FCM> fcms = fcmRepository.findByMemberId(memberId);
        sendPushNotifications(message, fcms);
        alarmService.addSingleAlarm(message, fcms.get(0).getMember());
    }

    @Transactional
    public void sendTopic(NotifyMessage message, String topic) {
        Message topicMessage = createTopicMessage(message, topic);
        firebaseMessagingClient.sendMessage(topicMessage, null);
        alarmService.addConsentBasedAlarms(message);
    }

    private void sendPushNotifications(final NotifyMessage message, final List<FCM> fcms) {
        // FCM 리스트를 Message 객체 리스트로 변환
        List<Pair<Message, FCM>> messagePairs = fcms.stream()
                .map(fcm -> Pair.of(createFCMMessage(message, fcm.getToken()), fcm))
                .toList();
        // 각 메시지를 전송하며 유효하지 않은 토큰 처리
        messagePairs.forEach(pair -> {
            firebaseMessagingClient.sendMessage(pair.getFirst(), pair.getSecond().getToken());
        });
    }

    private boolean isConsentGiven(NotifyMessage message, Long memberId) {
        List<Consent> consents = consentFacade.getConsents(memberId);

        Consent first = consents.stream()
                .filter(consent -> consent.getConsentType().equals(message.getConsentType()))
                .findFirst()
                .orElse(null);

        return first != null && first.isAgreed();
    }

    private Message createFCMMessage(NotifyMessage message, String token) {
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
