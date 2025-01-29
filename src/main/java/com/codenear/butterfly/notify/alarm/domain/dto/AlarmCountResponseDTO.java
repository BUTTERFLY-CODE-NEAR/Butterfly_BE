package com.codenear.butterfly.notify.alarm.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(title = "미확인 알림 JSON", description = "사용자 미확인 알림 개수 요청 시 반환하는 JSON")
public record AlarmCountResponseDTO(
        @Schema(description = "미확인 알림 개수") Long unreadAlarmCount) {
}
