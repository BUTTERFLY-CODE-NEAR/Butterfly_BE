package com.codenear.butterfly.payment.kakaoPay.domain.dto;

import com.codenear.butterfly.payment.domain.CancelConvertible;
import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.CanceledAmount;
import com.codenear.butterfly.payment.kakaoPay.domain.KakaoPaymentCancel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelResponseDTO implements CancelConvertible {

    private String aid;
    private String tid;
    private String cid;
    private String status;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;

    private Amount amount;
    private String item_name; // 상품명
    private String item_code; // 상품 코드
    private Integer quantity; // 상품 수량
    private String created_at; // 결제 요청 시간
    private String approved_at; // 결제 승인 시간
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달 내용

    @Override
    public CancelPayment toCancelPayment(Long memberId) {
        CancelPayment cancelPayment = KakaoPaymentCancel.kakaoPaymentBuilder()
                .cancelResponseDTO(this)
                .buildKakaoPayment();

        CanceledAmount canceledAmount = CanceledAmount.kakaoPaymentBuilder()
                .kakaoPaymentcancelResponseDTO(this)
                .buildKakaoPayment();

        cancelPayment.addCanceledAmount(canceledAmount);

        return cancelPayment;
    }

    @Getter
    public static class Amount {
        private Integer total;
        private Integer tax_free;
        private Integer vat;
        private Integer point;
        private Integer discount;
    }
}
