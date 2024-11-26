package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.fcm.domain.FCM;
import com.codenear.butterfly.fcm.domain.FCMRepository;
import com.codenear.butterfly.member.application.MemberFacade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final ConsentFacade consentFacade;
    private final MemberFacade memberFacade;
    private final FCMRepository fcmRepository;
    private final FirebaseMessagingClient firebaseMessagingClient;

    @Transactional
    protected void saveFCM(String token, MemberDTO loginMember) {
        Member member = memberFacade.getMember(loginMember.getId());
        List<Consent> consents = consentFacade.getConsentByMemberId(member.getId());

        createAndSaveFCM(token, member);
        subscribeToConsentedTopics(token, consents);
    }

    @Transactional
    protected void sendFCM(String title, String body, Long memberId) {
        List<FCM> fcms = fcmRepository.findByMemberId(memberId);

        fcms.forEach(fcm -> {
                    Message message = createMessage(title, body, fcm);
                    firebaseMessagingClient.sendMessage(message);
                });
    }

    private void createAndSaveFCM(String token, Member member) {
        FCM fcm = FCM.builder()
                .member(member)
                .token(token)
                .lastUsedDate(LocalDateTime.now())
                .build();
        fcmRepository.save(fcm);
    }

    private void subscribeToConsentedTopics(String token, List<Consent> consents) {
        List<String> tokens = List.of(token);
        consents.stream()
                .filter(Consent::isAgreed)
                .forEach(consent -> {
                    String topic = consent.getConsentType().getTopic();
                    firebaseMessagingClient.subscribeToTopic(tokens, topic);
                });
    }

    private Message createMessage(String title, String body, FCM fcm) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(fcm.getToken())
                .build();
    }
}
