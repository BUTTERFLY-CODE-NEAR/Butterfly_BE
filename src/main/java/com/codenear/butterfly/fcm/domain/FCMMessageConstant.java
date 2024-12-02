package com.codenear.butterfly.fcm.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FCMMessageConstant {
    INQUIRY("질문 답변 완료 ‼\uFE0F", "질문한 내용에 답변이 도착했습니다.", "inquiry");

    private final String title;
    private final String body;
    private final String key; // 페이로드
}
