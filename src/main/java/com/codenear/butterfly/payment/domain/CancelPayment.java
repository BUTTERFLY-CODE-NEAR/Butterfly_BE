package com.codenear.butterfly.payment.domain;

import com.codenear.butterfly.payment.kakaoPay.domain.dto.CancelResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.CancelResponseDTO.CancelDetail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@DiscriminatorColumn(name = "provider", discriminatorType = DiscriminatorType.STRING)
public class CancelPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tid;
    private String status;
    private String partnerOrderId; // 가맹점 주문번호
    private String partnerUserId; // 가맹점 회원 id
    private String paymentMethodType; // 결제 수단(CARD 또는 MONEY)

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "canceled_payment_id")
    private CanceledAmount canceledAmount;

    private String itemName; // 상품 이름
    private Integer quantity; // 상품 수량
    private String createdAt; // 결제 준비 요청 시간
    private String approvedAt; // 결제 승인 시간

    public CancelPayment(CancelResponseDTO cancelResponseDTO) {
        this.tid = cancelResponseDTO.getTid();
        this.status = cancelResponseDTO.getStatus();
        this.partnerOrderId = cancelResponseDTO.getPartner_order_id();
        this.partnerUserId = cancelResponseDTO.getPartner_user_id();
        this.paymentMethodType = cancelResponseDTO.getPayment_method_type();
        this.itemName = cancelResponseDTO.getItem_name();
        this.quantity = cancelResponseDTO.getQuantity();
        this.createdAt = cancelResponseDTO.getCreated_at();
        this.approvedAt = cancelResponseDTO.getApproved_at();
    }

    public CancelPayment(com.codenear.butterfly.payment.tossPay.domain.dto.CancelResponseDTO cancelResponseDTO, Long memberId) {
        for (CancelDetail cancel : cancelResponseDTO.getCancels()) {
            this.tid = cancel.getTransactionKey();
            this.status = cancel.getCancelStatus();
            this.partnerOrderId = cancelResponseDTO.getOrderId();
            this.partnerUserId = String.valueOf(memberId);
            this.paymentMethodType = cancelResponseDTO.getMethod();
            this.itemName = cancelResponseDTO.getOrderName();
            this.quantity = cancelResponseDTO.getQuantity();
            this.createdAt = cancelResponseDTO.getRequestedAt();
            this.approvedAt = cancel.getCanceledAt();
        }

    }

    @Builder(builderMethodName = "freeOrderBuilder", buildMethodName = "buildFreeOrder")
    public CancelPayment(OrderDetails orderDetails) {
        this.tid = orderDetails.getTid();
        this.status = "CANCEL_PAYMENT";
        this.partnerUserId = String.valueOf(orderDetails.getMember().getId());
        this.paymentMethodType = "MONEY";
        this.itemName = orderDetails.getProductName();
        this.quantity = orderDetails.getQuantity();
        this.createdAt = getCurrentDateTimeFormatted();
        this.approvedAt = getCurrentDateTimeFormatted();
    }

    public void addCanceledAmount(CanceledAmount canceledAmount) {
        this.canceledAmount = canceledAmount;
    }

    private String getCurrentDateTimeFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.now().format(formatter);
    }
}
