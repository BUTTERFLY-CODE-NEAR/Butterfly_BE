package com.codenear.butterfly.payment.domain;

import com.codenear.butterfly.payment.kakaoPay.domain.dto.CancelResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.CancelResponseDTO.CancelDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class CanceledAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer total; // 총 결제 금액
    private Integer taxFree; // 비과세 금액
    private Integer vat; // 부가세 금액
    private Integer point; // 사용한 포인트 금액
    private Integer discount; // 할인 금액

    @OneToOne(mappedBy = "canceledAmount")
    private CancelPayment cancelPayment;

    @Builder(builderMethodName = "kakaoPaymentBuilder", buildMethodName = "buildKakaoPayment")
    public CanceledAmount(CancelResponseDTO kakaoPaymentcancelResponseDTO) {
        this.total = kakaoPaymentcancelResponseDTO.getAmount().getTotal();
        this.taxFree = kakaoPaymentcancelResponseDTO.getAmount().getTax_free();
        this.vat = kakaoPaymentcancelResponseDTO.getAmount().getVat();
        this.point = kakaoPaymentcancelResponseDTO.getAmount().getPoint();
        this.discount = kakaoPaymentcancelResponseDTO.getAmount().getDiscount();
    }

    @Builder(builderMethodName = "tossPaymentBuilder", buildMethodName = "buildTossPayment")
    public CanceledAmount(com.codenear.butterfly.payment.tossPay.domain.dto.CancelResponseDTO tossPaymentcancelResponseDTO) {
        for (CancelDetail cancel : tossPaymentcancelResponseDTO.getCancels()) {
            this.total = cancel.getCancelAmount();
            this.taxFree = cancel.getTaxFreeAmount();
            this.vat = tossPaymentcancelResponseDTO.getVat();
            this.point = 0;
            this.discount = tossPaymentcancelResponseDTO.getEasyPay() != null ? cancel.getEasyPayDiscountAmount() : cancel.getTransferDiscountAmount();
        }
    }

    @Builder(builderMethodName = "freeOrderBuilder", buildMethodName = "buildFreeOrder")
    public CanceledAmount(OrderDetails orderDetails) {
        this.total = orderDetails.getTotal();
        this.taxFree = 0;
        this.vat = 0;
        this.point = 0;
        this.discount = 0;
    }
}
