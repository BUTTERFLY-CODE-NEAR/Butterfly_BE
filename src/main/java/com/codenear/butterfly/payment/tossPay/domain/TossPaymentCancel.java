package com.codenear.butterfly.payment.tossPay.domain;

import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.tossPay.domain.dto.CancelResponseDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TOSS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TossPaymentCancel extends CancelPayment {
    private String cancelReason;
    private String mId;

    @Builder(builderMethodName = "tossPaymentBuilder", buildMethodName = "buildTossPayment")
    public TossPaymentCancel(CancelResponseDTO cancelResponseDTO, Long memberId) {
        super(cancelResponseDTO, memberId);
        this.cancelReason = cancelResponseDTO.getCancels().get(0).getCancelReason();
        this.mId = cancelResponseDTO.getMId();
    }
}
