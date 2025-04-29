package com.codenear.butterfly.payment.domain.dto.handler;

import com.codenear.butterfly.payment.domain.CancelConvertible;
import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.OrderDetails;
import lombok.Getter;

@Getter
public class CancelPaymentHandler<T extends CancelConvertible> extends CancelHandler {
    private final T cancelResponseDTO;

    public CancelPaymentHandler(T cancelResponseDTO, OrderDetails orderDetails) {
        super(orderDetails);
        this.cancelResponseDTO = cancelResponseDTO;
    }

    @Override
    public CancelPayment createCancelPayment(Long memberId) {
        return cancelResponseDTO.toCancelPayment(memberId);
    }
}
