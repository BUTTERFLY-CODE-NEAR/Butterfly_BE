package com.codenear.butterfly.member.domain;

import lombok.Getter;

@Getter
public enum Grade {
    EGG("알", 0, 0),          // 4개월 동안 주문내역 없음
    LARBA("꼬물이", 1, 100000),   // 4개월 누적 주문 금액 10만원 미만
    PUPA("허물이", 2, 300000),     // 4개월 누적 주문 금액 10만원 이상, 30만원 미만
    BUTTERFLY("나비", 3, Integer.MAX_VALUE),  // 4개월 누적 주문 금액 30만원 이상

    ADMIN("관리자",400,0);

    private final String koreanName;
    private final int level;
    private final int maxAmount;

    Grade(String koreanName, int level, int maxAmount) {
        this.koreanName = koreanName;
        this.level = level;
        this.maxAmount = maxAmount;
    }
}
