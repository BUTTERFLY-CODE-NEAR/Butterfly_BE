package com.codenear.butterfly.notify.alarm.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.infrastructure.ConsentDataAccess;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmCountResponseDTO;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmsResponse;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRedisRepository;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final AlarmRedisRepository alarmRedisRepository;
    private final ConsentDataAccess consentDataAccess;

    public AlarmsResponse getAlarmsByMemberId(Long memberId) {
        List<Alarm> alarms = alarmRepository.findByMemberId(memberId);
        alarmRepository.markAllAsReadByMemberId(memberId); // 전체 읽음 처리
        alarmRedisRepository.readAlarm(memberId); // 미확인 알림 개수 초기화
        return AlarmsResponse.of(alarms);
    }

    public void addSingleAlarm(NotifyMessage message, Member member) {
        Alarm alarm = Alarm.builder()
                .message(message)
                .member(member)
                .build();
        alarmRepository.save(alarm);
    }

    public void addConsentBasedAlarms(NotifyMessage message) {
        List<Consent> consents = consentDataAccess.findConsentsByConsentType(message.getConsentType());
        consents.forEach(consent -> addSingleAlarm(message, consent.getMember()));
    }

    /**
     * Redis에서 사용자 미확인 알림 개수를 가져온다.
     *
     * @param memberId 멤버 아이디
     * @return 미확인 알림 개수 반환 ResponseDTO
     */
    public AlarmCountResponseDTO getAlarmCountByMember(Long memberId) {
        Long unreadCount = alarmRedisRepository.getTotalUnreadAlarms(memberId);
        return AlarmCountResponseDTO.builder()
                .unreadAlarmCount(unreadCount)
                .build();
    }
}
