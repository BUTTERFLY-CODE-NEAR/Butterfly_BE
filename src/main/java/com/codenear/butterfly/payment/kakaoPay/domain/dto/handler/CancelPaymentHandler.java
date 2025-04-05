package com.codenear.butterfly.payment.kakaoPay.domain.dto.handler;

import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.CanceledAmount;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.CancelResponseDTO;
import lombok.Getter;

@Getter
public class CancelPaymentHandler extends CancelHandler {
    private final CancelResponseDTO cancelResponseDTO;

    public CancelPaymentHandler(CancelResponseDTO cancelResponseDTO, OrderDetails orderDetails) {
        super(orderDetails);
        this.cancelResponseDTO = cancelResponseDTO;
    }

    @Override
    public CancelPayment createCancelPayment() {
        CancelPayment cancelPayment = CancelPayment.builder()
                .cancelResponseDTO(cancelResponseDTO)
                .build();

        CanceledAmount canceledAmount = CanceledAmount.builder()
                .cancelResponseDTO(cancelResponseDTO)
                .build();

        cancelPayment.addCanceledAmount(canceledAmount);
        return cancelPayment;
    }
}
