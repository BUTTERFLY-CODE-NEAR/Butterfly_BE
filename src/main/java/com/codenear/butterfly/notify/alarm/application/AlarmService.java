package com.codenear.butterfly.notify.alarm.application;

import com.codenear.butterfly.notify.alarm.domain.Alarm;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmsResponse;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmsResponse getAlarmsByMemberId(Long memberId) {
        List<Alarm> alarms = alarmRepository.findByMemberId(memberId);
        return AlarmsResponse.of(alarms);
    }
}
