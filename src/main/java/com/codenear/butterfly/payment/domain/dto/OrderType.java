package com.codenear.butterfly.payment.domain.dto;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.payment.exception.PaymentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderType {

    PICKUP("pickup"),
    DELIVER("deliver");

    private final String type;

    public static OrderType fromType(String type) {
        for (OrderType orderType : values()) {
            if (orderType.getType().equalsIgnoreCase(type)) {
                return orderType;
            }
        }
        throw new PaymentException(ErrorCode.SERVER_ERROR, null);
    }

}
