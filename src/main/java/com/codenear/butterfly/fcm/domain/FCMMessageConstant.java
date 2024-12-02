package com.codenear.butterfly.fcm.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FCMMessageConstant {
    INQUIRY("질문 답변 완료 ‼\uFE0F", "질문한 내용에 답변이 도착했습니다.", "inquiry"),
    NEW_PRODUCT("놓치지 마세요! 신규 상품 입고 \uD83C\uDF81", "나비에 신상품이 추가됐습니다. 지금 확인하고 가장 먼저 만나보세요 \uD83C\uDF1F", "new");

    private final String title;
    private final String body;
    private final String key; // 페이로드
}
