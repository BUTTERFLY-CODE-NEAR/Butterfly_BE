package com.codenear.butterfly.notify.alarm.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.infrastructure.ConsentDataAccess;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmsResponse;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ConsentDataAccess consentDataAccess;

    public AlarmsResponse getAlarmsByMemberId(Long memberId) {
        List<Alarm> alarms = alarmRepository.findByMemberId(memberId);
        alarmRepository.markAllAsReadByMemberId(memberId); // 전체 읽음 처리
        return AlarmsResponse.of(alarms);
    }

    public void addSingleAlarm(NotifyMessage message, Member member) {
        alarmRepository.save(createAlarm(message, member));
    }

    public void addConsentBasedAlarms(NotifyMessage message) {
        List<Consent> consents = consentDataAccess.findConsentsByConsentType(message.getConsentType());
        consents.forEach(consent -> addSingleAlarm(message, consent.getMember()));
    }

    private static Alarm createAlarm(final NotifyMessage message, final Member member) {
        return Alarm.builder()
                .notifyMessage(message)
                .member(member)
                .isNew(true)
                .build();
    }
}
