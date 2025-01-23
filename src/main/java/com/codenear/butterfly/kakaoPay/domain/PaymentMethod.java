package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.kakaoPay.exception.KakaoPayException;

public enum PaymentMethod {
    CARD,
    MONEY;

    /**
     * String 값을 기반으로 Enum 값을 반환하는 메서드
     *
     * @param name 문자열 값
     * @return 해당 문자열에 매핑되는 Enum 값
     * @throws IllegalArgumentException 매핑되는 Enum 값이 없을 경우 예외 발생
     */
    public static PaymentMethod fromString(String name) {
        for (PaymentMethod type : PaymentMethod.values()) {
            if (type.name().equalsIgnoreCase(name)) { // 대소문자 구분 없이 비교
                return type;
            }
        }
        throw new KakaoPayException(ErrorCode.INVALID_PAYMENT_METHOD, "결제 방식을 확인해주세요" + name);
    }
}
