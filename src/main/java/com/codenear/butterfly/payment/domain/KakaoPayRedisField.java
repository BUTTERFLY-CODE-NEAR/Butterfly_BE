package com.codenear.butterfly.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum KakaoPayRedisField {
    // 결제 필드
    ORDER_ID("orderId"),
    ORDER_TYPE("orderType"),
    TRANSACTION_ID("transactionId"),
    ADDRESS_ID("addressId"),
    OPTION_NAME("optionName"),
    PAYMENT_STATUS("paymentStatus"),
    PICKUP_PLACE("pickupPlace"),
    PICKUP_DATE("pickupDate"),
    PICKUP_TIME("pickupTime"),
    POINT("point"),
    DELIVER_DATE("deliverDate"),

    // 재고 예약 필드
    PRODUCT_NAME("productName"),
    QUANTITY("quantity");

    private String fieldName;
}
