package com.codenear.butterfly.fcm.application;

import static org.mockito.Mockito.verify;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FirebaseMessagingClientTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FirebaseMessagingClient firebaseMessagingClient;

    @Test
    void Token으로_메시지를_전송한다() throws FirebaseMessagingException {
        // given
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("테스트 제목")
                        .setBody("테스트 메시지")
                        .build())
                .setToken("dummyToken")
                .build();

        // when
        firebaseMessagingClient.sendMessage(message);

        // then
        verify(firebaseMessaging)
                .send(message);
    }

    @Test
    void Topic으로_메시지를_전송한다() throws FirebaseMessagingException {
        // given
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("테스트 제목")
                        .setBody("테스트 메시지")
                        .build())
                .setTopic("testTopic")
                .build();

        // when
        firebaseMessagingClient.sendMessage(message);

        // then
        verify(firebaseMessaging)
                .send(message);
    }

    @Test
    void Token으로_특정_Topic에_구독한다() throws FirebaseMessagingException {
        // given
        List<String> tokens = List.of("dummy_token");
        String topic = "test";

        // when
        firebaseMessagingClient.subscribeToTopic(tokens, topic);

        // then
        verify(firebaseMessaging)
                .subscribeToTopic(tokens, topic);
    }

    @Test
    void Token으로_특정_Topic에_구독을_해지한다() throws FirebaseMessagingException {
        // given
        List<String> tokens = List.of("dummy_token");
        String topic = "test";

        // when
        firebaseMessagingClient.unsubscribeFromTopic(tokens, topic);

        // then
        verify(firebaseMessaging)
                .unsubscribeFromTopic(tokens, topic);
    }
}