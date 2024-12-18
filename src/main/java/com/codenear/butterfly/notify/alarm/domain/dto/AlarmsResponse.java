package com.codenear.butterfly.notify.alarm.domain.dto;

import com.codenear.butterfly.notify.alarm.domain.Alarm;
import java.util.List;

public record AlarmsResponse(
        List<AlarmResponse> alarmResponses
) {
    
    public static AlarmsResponse of(final List<Alarm> alarms) {
        return new AlarmsResponse(createAlarmResponses(alarms));
    }

    private static List<AlarmResponse> createAlarmResponses(final List<Alarm> alarms) {
        return alarms.stream()
                .map(AlarmResponse::of)
                .toList();
    }
}
