package com.codenear.butterfly.payment.domain;

public interface CancelConvertible {
    CancelPayment toCancelPayment(Long memberId);
}
