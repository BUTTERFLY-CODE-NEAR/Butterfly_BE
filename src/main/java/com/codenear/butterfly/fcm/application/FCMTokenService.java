package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.fcm.domain.FCM;
import com.codenear.butterfly.fcm.domain.FCMRepository;
import com.codenear.butterfly.member.application.MemberFacade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMTokenService {

    private final ConsentFacade consentFacade;
    private final MemberFacade memberFacade;
    private final FCMRepository fcmRepository;
    private final FirebaseMessagingClient firebaseMessagingClient;

    @Transactional
    protected void saveFCM(String token, MemberDTO loginMember) {
        Member member = memberFacade.getMember(loginMember.getId());
        List<Consent> consents = consentFacade.getConsentByMemberId(member.getId());

        FCM fcm = createFCM(token, member);
        subscribeToConsentedTopics(token, consents);

        fcmRepository.save(fcm);
    }

    protected void subscribeToTopic(Long memberId, String topic) {
        List<String> tokens = getTokensByMemberId(memberId);
        firebaseMessagingClient.subscribeToTopic(tokens, topic);
    }

    protected void unsubscribeFromTopic(Long memberId, String topic) {
        List<String> tokens = getTokensByMemberId(memberId);
        firebaseMessagingClient.unsubscribeFromTopic(tokens, topic);
    }

    private FCM createFCM(String token, Member member) {
        return FCM.builder()
                .member(member)
                .token(token)
                .lastUsedDate(LocalDateTime.now())
                .build();
    }

    private void subscribeToConsentedTopics(String token, List<Consent> consents) {
        List<String> tokens = List.of(token);
        consents.stream()
                .filter(Consent::isTopicConsented)
                .forEach(consent -> {
                    String topic = consent.getConsentType().getTopic();
                    firebaseMessagingClient.subscribeToTopic(tokens, topic);
                });
    }

    private List<String> getTokensByMemberId(Long memberId) {
        List<FCM> fcms = fcmRepository.findByMemberId(memberId);
        return fcms.stream()
                .map(FCM::getToken)
                .toList();
    }
}
