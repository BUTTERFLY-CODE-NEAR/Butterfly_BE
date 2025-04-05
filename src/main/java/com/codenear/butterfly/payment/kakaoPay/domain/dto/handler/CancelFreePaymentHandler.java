package com.codenear.butterfly.payment.kakaoPay.domain.dto.handler;

import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.CanceledAmount;
import com.codenear.butterfly.payment.domain.OrderDetails;

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
