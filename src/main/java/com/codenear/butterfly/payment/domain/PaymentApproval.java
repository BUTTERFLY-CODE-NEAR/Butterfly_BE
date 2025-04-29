package com.codenear.butterfly.payment.domain;

import java.util.Optional;

public interface PaymentApproval {
    String getOrderId();

    String getProductName();

    int getQuantity();

    String getPaymentMethod();

    SinglePayment toSinglePayment(Long memberId);

    Amount toAmount();

    Optional<CardInfo> toCardInfo();
}
