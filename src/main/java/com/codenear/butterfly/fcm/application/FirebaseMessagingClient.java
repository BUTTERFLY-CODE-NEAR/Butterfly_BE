package com.codenear.butterfly.fcm.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.fcm.exception.FCMException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FirebaseMessagingClient {

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessage(Message message) {
        handleFirebaseOperation(firebaseMessaging::send, message);
    }

    public void subscribeToTopic(List<String> tokens, String topic) {
        handleFirebaseOperation((t) ->
                firebaseMessaging.subscribeToTopic(tokens, t), topic);
    }

    public void unsubscribeFromTopic(List<String> tokens, String topic) {
        handleFirebaseOperation((t) ->
                firebaseMessaging.unsubscribeFromTopic(tokens, t), topic);
    }

    private <T> void handleFirebaseOperation(FirebaseOperation<T> operation, T input) {
        try {
            operation.execute(input);
        } catch (FirebaseMessagingException e) {
            throw new FCMException(SERVER_ERROR, e.getMessagingErrorCode());
        }
    }

    @FunctionalInterface
    private interface FirebaseOperation<T> {
        void execute(T param) throws FirebaseMessagingException;
    }
}
