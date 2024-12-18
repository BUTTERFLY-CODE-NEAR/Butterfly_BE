package com.codenear.butterfly.notify.alarm.domain.dto;

import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import java.time.LocalDateTime;

public record AlarmResponse(
        Long id,
        Info info,
        boolean isNew
) {
    private record Info(
            String title,
            String subtitle,
            String content,
            LocalDateTime createdAt
    ) {
    }

    public static AlarmResponse of(final Alarm alarm) {
        Info info = createInfo(alarm);
        return new AlarmResponse(
                alarm.getId(),
                info,
                alarm.isNew()
        );
    }

    private static Info createInfo(final Alarm alarm) {
        NotifyMessage message = alarm.getNotifyMessage();
        return new Info(
                message.getTitle(),
                message.getSubtitle(),
                message.getContent(),
                alarm.getCreatedAt()
        );
    }
}
