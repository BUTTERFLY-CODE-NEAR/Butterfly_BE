package com.codenear.butterfly.notify.fcm.infrastructure;

import com.codenear.butterfly.notify.exception.NotifyException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class FirebaseMessagingClient {

    private final FirebaseMessaging firebaseMessaging;
    private final FCMRepository fcmRepository;

    public void sendMessage(Message message, String token) {
        handleFirebaseOperation(firebaseMessaging::send, message, token);
    }

    public void subscribeToTopic(List<String> tokens, String topic) {
        handleFirebaseOperation((t) ->
                firebaseMessaging.subscribeToTopic(tokens, t), topic, null);
    }

    public void unsubscribeFromTopic(List<String> tokens, String topic) {
        handleFirebaseOperation((t) ->
                firebaseMessaging.unsubscribeFromTopic(tokens, t), topic, null);
    }

    private <T> void handleFirebaseOperation(FirebaseOperation<T> operation, T input, String token) {
        try {
            operation.execute(input);
        } catch (FirebaseMessagingException e) {
            if (isInvalidTokenException(e)) {
                fcmRepository.deleteByToken(token);
            } else {
                throw new NotifyException(SERVER_ERROR, e.getMessagingErrorCode());
            }
        }
    }

    /**
     * 토큰 유효성 검증
     *
     * @param e 예외 메세지
     * @return 유효성검증 결과 (Boolean)
     */
    private boolean isInvalidTokenException(FirebaseMessagingException e) {
        String errorCode = String.valueOf(e.getErrorCode());
        return errorCode != null &&
                (errorCode.equals("NOT_FOUND") ||
                        errorCode.equals("INVALID_ARGUMENT") ||
                        errorCode.equals("UNREGISTERED"));
    }

    @FunctionalInterface
    private interface FirebaseOperation<T> {
        void execute(T param) throws FirebaseMessagingException;
    }
}
