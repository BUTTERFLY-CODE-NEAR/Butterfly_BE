package com.codenear.butterfly.payment.domain.dto.handler;

import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.CardInfo;
import com.codenear.butterfly.payment.domain.PaymentApproval;
import com.codenear.butterfly.payment.domain.SinglePayment;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ApprovePaymentHandler<T extends PaymentApproval> extends ApproveHandler {
    private final T approveResponseDTO;

    public ApprovePaymentHandler(T approveResponseDTO, int point) {
        super(
                approveResponseDTO.getOrderId(),
                approveResponseDTO.getProductName(),
                approveResponseDTO.getQuantity(),
                point);
        this.approveResponseDTO = approveResponseDTO;
    }

    @Override
    public SinglePayment createSinglePayment(Long memberId) {
        return approveResponseDTO.toSinglePayment(memberId);
    }

    @Override
    public Amount createAmount() {
        return approveResponseDTO.toAmount();
    }

    @Override
    public Object getOrderDetailDto() {
        return approveResponseDTO;
    }

    @Override
    public Optional<CardInfo> createCardInfo() {
        return approveResponseDTO.toCardInfo();
    }
}
