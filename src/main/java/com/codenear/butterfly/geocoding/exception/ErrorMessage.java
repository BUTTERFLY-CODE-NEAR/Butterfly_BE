package com.codenear.butterfly.geocoding.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorMessage {
    RESPONSE_BODY_NULL("응답 본문 값이 NULL 입니다."),
    RESPONSE_BODY_DATA_NULL("응답 본문 값의 데이터가 없습니다. - 잘못된 주소 입력");

    private final String message;
}
