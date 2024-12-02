package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.fcm.domain.FCMMessageConstant;
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
    protected void send(FCMMessageConstant fcmMessageConstant, Long memberId) {
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
