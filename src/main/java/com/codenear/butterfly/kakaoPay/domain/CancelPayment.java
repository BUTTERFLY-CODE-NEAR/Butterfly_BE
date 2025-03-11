package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
import jakarta.persistence.CascadeType;
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
public class CancelPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aid; // 요청 고유 번호
    private String tid; // 결제 고유 번호
    private String cid; // 가맹점 코드
    private String status;
    private String partnerOrderId; // 가맹점 주문번호
    private String partnerUserId; // 가맹점 회원 id
    private String paymentMethodType; // 결제 수단(CARD 또는 MONEY)

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "canceled_payment_id")
    private CanceledAmount canceledAmount;

    private String itemName; // 상품 이름
    private String itemCode; // 상품 코드
    private Integer quantity; // 상품 수량
    private String createdAt; // 결제 준비 요청 시간
    private String approvedAt; // 결제 승인 시간
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달된 내용

    @Builder
    public CancelPayment(CancelResponseDTO cancelResponseDTO) {
        this.aid = cancelResponseDTO.getAid();
        this.tid = cancelResponseDTO.getTid();
        this.cid = cancelResponseDTO.getCid();
        this.status = cancelResponseDTO.getStatus();
        this.partnerOrderId = cancelResponseDTO.getPartner_order_id();
        this.partnerUserId = cancelResponseDTO.getPartner_user_id();
        this.paymentMethodType = cancelResponseDTO.getPayment_method_type();
        this.itemName = cancelResponseDTO.getItem_name();
        this.itemCode = cancelResponseDTO.getItem_code();
        this.quantity = cancelResponseDTO.getQuantity();
        this.createdAt = cancelResponseDTO.getCreated_at();
        this.approvedAt = cancelResponseDTO.getApproved_at();
        this.payload = cancelResponseDTO.getPayload();
    }

    @Builder(builderMethodName = "freeOrderBuilder", buildMethodName = "buildFreeOrder")
    public CancelPayment(OrderDetails orderDetails) {
        this.tid = orderDetails.getTid();
        this.status = "CANCEL_PAYMENT";
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
