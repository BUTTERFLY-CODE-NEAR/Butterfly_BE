package com.codenear.butterfly.payment.tossPay.domain.dto;

import com.codenear.butterfly.payment.domain.CancelConvertible;
import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.CanceledAmount;
import com.codenear.butterfly.payment.tossPay.domain.TossPaymentCancel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class CancelResponseDTO extends ConfirmResponseDTO implements CancelConvertible {
    private List<CancelDetail> cancels;

    @Override
    public CancelPayment toCancelPayment(Long memberId) {
        CancelPayment cancelPayment = TossPaymentCancel.tossPaymentBuilder()
                .cancelResponseDTO(this)
                .memberId(memberId)
                .buildTossPayment();
        CanceledAmount canceledAmount = CanceledAmount.tossPaymentBuilder()
                .tossPaymentcancelResponseDTO(this)
                .buildTossPayment();

        cancelPayment.addCanceledAmount(canceledAmount);

        return cancelPayment;
    }

    @Getter
    @NoArgsConstructor
    public static class CancelDetail {
        private String transactionKey; // 취소 건의 키값
        private String cancelReason; // 취소 이유
        private Integer taxExemptionAmount; // 취소된 금액 중 과세 제외 금액(컵 보증금 등)
        private String canceledAt; // 결제 취소가 일어난 날짜
        private Integer transferDiscountAmount; // 퀵 계좌이체 서비스의 즉시할인에서 취소된 금액
        private Integer easyPayDiscountAmount; // 간편결제 서비스의 포인트, 쿠폰, 즉시할인과 같은 적립식 결제수단에서 취소된 금액
        private String receiptKey; // 취소 건의 현금영수증 키 값
        private Integer cancelAmount; // 취소 금액
        private Integer taxFreeAmount; // 취소된 금액 중 면세 금액
        private Integer refundableAmount; // 결제 취소 후 환불 가능한 잔액
        private String cancelStatus; // 취소 상태 (DONE 이면 결제가 성공적으로 취소된 상태)
    }
}
