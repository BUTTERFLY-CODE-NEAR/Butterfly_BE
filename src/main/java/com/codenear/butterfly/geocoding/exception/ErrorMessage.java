package com.codenear.butterfly.geocoding.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    RESPONSE_BODY_NULL("응답 본문 값이 NULL 입니다."),
    RESPONSE_TOTAL_COUNT_ZERO("응답 본문의 값이 없습니다. - 잘못된 주소 입력");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
