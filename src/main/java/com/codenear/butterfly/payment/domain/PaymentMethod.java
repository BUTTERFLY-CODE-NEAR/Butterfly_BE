package com.codenear.butterfly.payment.domain;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.payment.exception.PaymentException;

import java.util.Arrays;

public enum PaymentMethod {
    // KakaoMethod
    CARD,
    MONEY,

    // TossMethod
    카드,
    가상계좌,
    간편결제,
    휴대폰,
    계좌이체,
    문화상품권,
    도서문화상품권,
    게임문화상품권;

    /**
     * String 값을 기반으로 Enum 값을 반환하는 메서드
     *
     * @param name 문자열 값
     * @return 해당 문자열에 매핑되는 Enum 값
     * @throws IllegalArgumentException 매핑되는 Enum 값이 없을 경우 예외 발생
     */
    public static PaymentMethod fromString(String name) {
        return Arrays.stream(PaymentMethod.values())
                .filter(value -> value.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new PaymentException(ErrorCode.INVALID_PAYMENT_METHOD, String.format("결제 방식을 확인해주세요: %s", name)));
    }
}
