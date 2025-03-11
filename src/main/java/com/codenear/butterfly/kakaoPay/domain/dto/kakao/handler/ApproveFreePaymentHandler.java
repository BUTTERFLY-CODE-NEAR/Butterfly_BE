package com.codenear.butterfly.kakaoPay.domain.dto.kakao.handler;

import com.codenear.butterfly.kakaoPay.domain.Amount;
import com.codenear.butterfly.kakaoPay.domain.SinglePayment;
import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import lombok.Getter;

@Getter
public class ApproveFreePaymentHandler extends ApproveHandler {
    private final BasePaymentRequestDTO basePaymentRequestDTO;
    private final Long memberId;

    public ApproveFreePaymentHandler(BasePaymentRequestDTO basePaymentRequestDTO, String orderId, Long memberId) {
        super(
                orderId,
                basePaymentRequestDTO.getProductName(),
                basePaymentRequestDTO.getQuantity(),
                basePaymentRequestDTO.getPoint()
        );
        this.basePaymentRequestDTO = basePaymentRequestDTO;
        this.memberId = memberId;
    }

    @Override
    public SinglePayment createSinglePayment() {
        return SinglePayment.freeOrderBuilder()
                .partnerOrderId(orderId)
                .partnerUserId(memberId)
                .basePaymentRequestDTO(basePaymentRequestDTO)
                .buildFreeOrder();
    }

    @Override
    public Amount createAmount() {
        return Amount.freeOrderBuilder()
                .basePaymentRequestDTO(basePaymentRequestDTO)
                .buildFreeOrder();
    }

    @Override
    public Object getOrderDetailDto() {
        return basePaymentRequestDTO;
    }
}
