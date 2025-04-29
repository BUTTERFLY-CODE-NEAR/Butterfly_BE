package com.codenear.butterfly.payment.domain;

import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "provider", discriminatorType = DiscriminatorType.STRING)
public class SinglePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tid; // 결제 고유 번호
    private String orderId; // 가맹점 주문번호
    private String productName; // 상품 이름
    private String productCode; // 상품 코드
    private Integer quantity; // 상품 수량
    private String requestedAt; // 결제 준비 요청 시간
    private String approvedAt; // 결제 승인 시간
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달된 내용
    private Long memberId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethodType; // 결제 수단(CARD 또는 MONEY)

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "amount_id")
    private Amount amount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_info_id")
    private CardInfo cardInfo;

    public SinglePayment(ApproveResponseDTO approveResponseDTO, Long memberId) {
        this.tid = approveResponseDTO.getTid();
        this.orderId = approveResponseDTO.getPartner_order_id();
        this.memberId = memberId;
        this.paymentMethodType = PaymentMethod.fromString(approveResponseDTO.getPayment_method_type());
        this.productName = approveResponseDTO.getItem_name();
        this.productCode = approveResponseDTO.getItem_code();
        this.quantity = approveResponseDTO.getQuantity();
        this.requestedAt = approveResponseDTO.getCreated_at();
        this.approvedAt = approveResponseDTO.getApproved_at();
        this.payload = approveResponseDTO.getPayload();
    }

    public SinglePayment(ConfirmResponseDTO confirmResponseDTO, Long memberId) {
        this.tid = confirmResponseDTO.getPaymentKey();
        this.orderId = confirmResponseDTO.getOrderId();
        this.memberId = memberId;
        this.paymentMethodType = PaymentMethod.fromString(confirmResponseDTO.getMethod());
        this.productName = confirmResponseDTO.getOrderName();
        this.quantity = confirmResponseDTO.getQuantity();
        this.requestedAt = confirmResponseDTO.getRequestedAt();
        this.approvedAt = confirmResponseDTO.getApprovedAt();
        this.payload = confirmResponseDTO.getPayload();
    }

    @Builder(builderMethodName = "freeOrderBuilder", buildMethodName = "buildFreeOrder")
    public SinglePayment(String orderId, Long memberId, BasePaymentRequestDTO basePaymentRequestDTO) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.paymentMethodType = PaymentMethod.MONEY;
        this.productName = basePaymentRequestDTO.getProductName();
        this.quantity = basePaymentRequestDTO.getQuantity();
        this.requestedAt = String.valueOf(LocalDateTime.now());
        this.approvedAt = String.valueOf(LocalDateTime.now());
    }

    public void addAmount(Amount amount) {
        this.amount = amount;
    }

    public void addCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }
}