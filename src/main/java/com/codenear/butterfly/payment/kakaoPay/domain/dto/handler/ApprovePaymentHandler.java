package com.codenear.butterfly.payment.kakaoPay.domain.dto.handler;

import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.CardInfo;
import com.codenear.butterfly.payment.domain.PaymentMethod;
import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ApprovePaymentHandler extends ApproveHandler {
    private final ApproveResponseDTO approveResponseDTO;

    public ApprovePaymentHandler(ApproveResponseDTO approveResponseDTO, int point) {
        super(
                approveResponseDTO.getPartner_order_id(),
                approveResponseDTO.getItem_name(),
                approveResponseDTO.getQuantity(),
                point
        );
        this.approveResponseDTO = approveResponseDTO;
    }

    @Override
    public SinglePayment createSinglePayment() {
        return SinglePayment.builder()
                .approveResponseDTO(approveResponseDTO)
                .build();
    }

    @Override
    public Amount createAmount() {
        return Amount.builder()
                .approveResponseDTO(approveResponseDTO)
                .build();
    }

    @Override
    public Object getOrderDetailDto() {
        return approveResponseDTO;
    }

    @Override
    public Optional<CardInfo> createCardInfo() {
        if (approveResponseDTO.getPayment_method_type().equals(PaymentMethod.CARD.name())) {
            return Optional.of(CardInfo.builder().approveResponseDTO(approveResponseDTO).build());
        }
        return Optional.empty();
    }
}
