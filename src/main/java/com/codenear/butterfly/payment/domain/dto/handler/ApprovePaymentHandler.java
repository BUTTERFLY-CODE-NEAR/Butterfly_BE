package com.codenear.butterfly.payment.domain.dto.handler;

import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.CardInfo;
import com.codenear.butterfly.payment.domain.PaymentMethod;
import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ApprovePaymentHandler<T> extends ApproveHandler {
    private final T approveResponseDTO;

    public ApprovePaymentHandler(T approveResponseDTO, int point) {
        String orderId = "";
        String productName = "";
        int quantity = 0;
        if (approveResponseDTO instanceof ApproveResponseDTO kakaoApprove) {
            orderId = kakaoApprove.getPartner_order_id();
            productName = kakaoApprove.getItem_name();
            quantity = kakaoApprove.getQuantity();
        } else if (approveResponseDTO instanceof ConfirmResponseDTO tossApprove) {
            orderId = tossApprove.getOrderId();
            productName = tossApprove.getOrderName();
            quantity = tossApprove.getQuantity();
        }
        super(orderId, productName, quantity, point);
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
