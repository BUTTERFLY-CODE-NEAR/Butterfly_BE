package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.fcm.domain.FCM;
import com.codenear.butterfly.fcm.domain.FCMRepository;
import com.codenear.butterfly.member.application.MemberFacade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
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
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    protected void saveFCM(String token, MemberDTO loginMember) {
        Member member = memberFacade.getMember(loginMember.getId());
        List<Consent> consents = consentFacade.getConsentByMemberId(member.getId());

        createAndSaveFCM(token, member);
        subscribeToConsentedTopics(token, consents);
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
                    subscribeToTopic(tokens, topic);
                });
    }

    private void subscribeToTopic(List<String> tokens, String topic) {
        try {
            firebaseMessaging.subscribeToTopic(tokens, topic);
        } catch (FirebaseMessagingException e) {
            // 에러 처리 해야함
        }
    }

    private void unsubscribeFromTopic(List<String> tokens, String topic) {
        try {
            firebaseMessaging.unsubscribeFromTopic(tokens, topic);
        } catch (FirebaseMessagingException e) {
            // 에러 처리 해야함
        }
    }
}
