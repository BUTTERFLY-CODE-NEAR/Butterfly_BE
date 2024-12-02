package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.fcm.domain.FCMRepository;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMMessageService {

    private final FCMRepository fcmRepository;
    private final FirebaseMessagingClient firebaseMessagingClient;

    @Transactional
    protected void send(String title, String body, Long memberId) {
        fcmRepository.findByMemberId(memberId)
                .stream()
                .map(fcm -> createMessage(title, body, fcm.getToken()))
                .forEach(firebaseMessagingClient::sendMessage);
    }

    @Transactional
    protected void sendTopic(String title, String body, String topic) {
        Message topicMessage = createTopicMessage(title, body, topic);
        firebaseMessagingClient.sendMessage(topicMessage);
    }

    private Message createMessage(String title, String body, String token) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build();
    }

    private Message createTopicMessage(String title, String body, String topic) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setTopic(topic)
                .build();
    }
}
