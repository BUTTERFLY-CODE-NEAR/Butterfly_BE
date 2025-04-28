package com.codenear.butterfly.payment.tossPay.domain;

import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TOSS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TossPayment extends SinglePayment {
    private String mId; // 상점아이디 (토스에서 발급)
    private String status; // 결제 상태
    @Enumerated(value = EnumType.STRING)
    private TossPaymentType type; // 결제 타입 정보 NORMAL(일반결제), BILLING (자동결제), BRANDPAY (브랜드 페이)

    @Builder(builderMethodName = "tossPaymentBuilder", buildMethodName = "buildTossPayment")
    public TossPayment(ConfirmResponseDTO confirmResponseDTO, Long memberId) {
        super(confirmResponseDTO, memberId);
        this.mId = confirmResponseDTO.getMId();
        this.status = confirmResponseDTO.getStatus();
        this.type = confirmResponseDTO.getType();
    }
}
