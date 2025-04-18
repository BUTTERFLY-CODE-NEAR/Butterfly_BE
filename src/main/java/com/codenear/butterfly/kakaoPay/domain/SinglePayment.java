package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class SinglePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aid; // 요청 고유 번호
    private String tid; // 결제 고유 번호
    private String cid; // 가맹점 코드
    private String sid; // 정기 결제용 ID
    private String partnerOrderId; // 가맹점 주문번호
    private String partnerUserId; // 가맹점 회원 id

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethodType; // 결제 수단(CARD 또는 MONEY)

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "amount_id")
    private Amount amount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_info_id")
    private CardInfo cardInfo;

    private String itemName; // 상품 이름
    private String itemCode; // 상품 코드
    private Integer quantity; // 상품 수량
    private String createdAt; // 결제 준비 요청 시간
    private String approvedAt; // 결제 승인 시간
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달된 내용

    @Builder
    public SinglePayment(ApproveResponseDTO approveResponseDTO) {
        this.aid = approveResponseDTO.getAid();
        this.tid = approveResponseDTO.getTid();
        this.cid = approveResponseDTO.getCid();
        this.sid = approveResponseDTO.getSid();
        this.partnerOrderId = approveResponseDTO.getPartner_order_id();
        this.partnerUserId = approveResponseDTO.getPartner_user_id();
        this.paymentMethodType = PaymentMethod.fromString(approveResponseDTO.getPayment_method_type());
        this.itemName = approveResponseDTO.getItem_name();
        this.itemCode = approveResponseDTO.getItem_code();
        this.quantity = approveResponseDTO.getQuantity();
        this.createdAt = approveResponseDTO.getCreated_at();
        this.approvedAt = approveResponseDTO.getApproved_at();
        this.payload = approveResponseDTO.getPayload();
    }

    @Builder(builderMethodName = "freeOrderBuilder", buildMethodName = "buildFreeOrder")
    public SinglePayment(String partnerOrderId, Long partnerUserId, BasePaymentRequestDTO basePaymentRequestDTO) {
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = String.valueOf(partnerUserId);
        this.paymentMethodType = PaymentMethod.MONEY;
        this.itemName = basePaymentRequestDTO.getProductName();
        this.quantity = basePaymentRequestDTO.getQuantity();
        this.createdAt = String.valueOf(LocalDateTime.now());
        this.approvedAt = String.valueOf(LocalDateTime.now());
    }

    public void addAmount(Amount amount) {
        this.amount = amount;
    }

    public void addCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }
}