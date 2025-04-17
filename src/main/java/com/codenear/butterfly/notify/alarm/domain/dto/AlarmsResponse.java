package com.codenear.butterfly.notify.alarm.domain.dto;

import com.codenear.butterfly.notify.alarm.domain.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(title = "알림 목록 JSON", description = "알림 목록 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record AlarmsResponse(
        @Schema(description = "알림 목록") List<AlarmResponse> alarmResponses
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
