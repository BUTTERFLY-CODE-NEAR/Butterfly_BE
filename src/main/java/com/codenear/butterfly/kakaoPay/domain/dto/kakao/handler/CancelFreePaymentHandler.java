package com.codenear.butterfly.kakaoPay.domain.dto.kakao.handler;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import com.codenear.butterfly.kakaoPay.domain.CanceledAmount;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;

public class CancelFreePaymentHandler extends CancelHandler {
    public CancelFreePaymentHandler(OrderDetails orderDetails) {
        super(orderDetails);
    }

    @Override
    public CancelPayment createCancelPayment() {
        CancelPayment cancelPayment = CancelPayment.freeOrderBuilder()
                .orderDetails(orderDetails)
                .buildFreeOrder();

        CanceledAmount canceledAmount = CanceledAmount.freeOrderBuilder()
                .orderDetails(orderDetails)
                .buildFreeOrder();

        cancelPayment.addCanceledAmount(canceledAmount);
        return cancelPayment;
    }
}
