package com.codenear.butterfly.kakaoPay.domain.dto.kakao.handler;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import com.codenear.butterfly.kakaoPay.domain.CanceledAmount;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
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
