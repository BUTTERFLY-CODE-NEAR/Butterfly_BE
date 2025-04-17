package com.codenear.butterfly.notify.alarm.domain.dto;

import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(title = "알림 JSON", description = "알림 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record AlarmResponse(
        @Schema(description = "알림 ID") Long id,
        @Schema(description = "제목") String title,
        @Schema(description = "소제목") String subtitle,
        @Schema(description = "내용") String content,
        @Schema(description = "시간") LocalDateTime createdAt,
        @Schema(description = "새로운 알림 여부") boolean isNew
    ) {

    public static AlarmResponse of(final Alarm alarm) {
        NotifyMessage message = alarm.getNotifyMessage();
        return new AlarmResponse(
                alarm.getId(),
                message.getTitle(),
                message.getSubtitle(),
                message.getContent(),
                alarm.getCreatedAt(),
                alarm.isNew()
        );
    }
}
