package com.codenear.butterfly.notify.application;

import com.codenear.butterfly.consent.application.ConsentFacade;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.notify.alarm.application.AlarmService;
import com.codenear.butterfly.notify.fcm.application.FCMMessageService;
import com.codenear.butterfly.notify.fcm.domain.FCM;
import com.codenear.butterfly.notify.fcm.infrastructure.FCMRepository;
import com.codenear.butterfly.notify.fcm.infrastructure.FirebaseMessagingClient;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.codenear.butterfly.notify.NotifyMessage.INQUIRY_ANSWERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FCMMessageServiceTest {

    @Mock
    private ConsentFacade consentFacade;

    @Mock
    private FCMRepository fcmRepository;

    @Mock
    private FirebaseMessagingClient firebaseMessagingClient;

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private FCMMessageService fcmMessageService;

    @Test
    void 동의가_TRUE인_경우_전송한다() {
        // given
        Long memberId = 1L;

        mockConsent(true, memberId);
        mockFCMRepository(memberId);

        // when
        fcmMessageService.sendNotificationMessage(INQUIRY_ANSWERED, memberId);

        // then
        verify(fcmRepository).findByMemberId(memberId);
        verify(firebaseMessagingClient).sendMessage(any(Message.class));
    }

    @Test
    void 동의가_FALSE인_경우_전송하지_않는다() {
        // given
        Long memberId = 1L;

        mockConsent(false, memberId);

        // when
        fcmMessageService.sendNotificationMessage(INQUIRY_ANSWERED, memberId);

        // then
        verify(fcmRepository, never()).findByMemberId(memberId);
        verify(firebaseMessagingClient, never()).sendMessage(any(Message.class));
    }

    private void mockFCMRepository(Long memberId) {
        FCM fcm = FCM.builder()
                .token("dummy_token")
                .build();

        when(fcmRepository.findByMemberId(memberId))
                .thenReturn(List.of(fcm));
    }

    private void mockConsent(boolean isAgreed, Long memberId) {
        Consent consent = Consent.builder()
                .consentType(ConsentType.MARKETING)
                .isAgreed(isAgreed)
                .build();

        when(consentFacade.getConsents(memberId))
                .thenReturn(List.of(consent));
    }
}