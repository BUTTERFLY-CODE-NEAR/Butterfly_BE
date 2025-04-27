package com.codenear.butterfly.payment.kakaoPay.domain.dto;

import com.codenear.butterfly.payment.domain.PaymentApproval;
import com.codenear.butterfly.payment.domain.PaymentMethod;
import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.kakaoPay.domain.KakaoPayment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@Setter
@ToString
public class ApproveResponseDTO implements PaymentApproval {

    private String aid; // 요청 고유 번호
    private String tid; // 결제 고유 번호
    private String cid; // 가맹점 코드
    private String sid; // 정기결제용 ID
    private String partner_order_id; // 가맹점 주문 번호
    private String partner_user_id; // 가맹점 회원 id
    private String payment_method_type; // 결제 수단

    private Amount amount;
    private CardInfo card_info;
    private String item_name; // 상품명
    private String item_code; // 상품 코드
    private Integer quantity; // 상품 수량
    private String created_at; // 결제 요청 시간
    private String approved_at; // 결제 승인 시간
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달 내용

    @Override
    public String getOrderId() {
        return partner_order_id;
    }

    @Override
    public String getProductName() {
        return item_name;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String getPaymentMethod() {
        return payment_method_type;
    }

    @Override
    public SinglePayment toSinglePayment(Long memberId) {
        return KakaoPayment.kakaoPaymentBuilder()
                .approveResponseDTO(this)
                .memberId(memberId)
                .buildKakaoPayment();
    }

    @Override
    public com.codenear.butterfly.payment.domain.Amount toAmount() {
        return com.codenear.butterfly.payment.domain.Amount.kakaoPaymentBuilder()
                .approveResponseDTO(this)
                .buildKakaoPayment();
    }

    @Override
    public Optional<com.codenear.butterfly.payment.domain.CardInfo> toCardInfo() {
        if (this.getPaymentMethod().equals(PaymentMethod.CARD.name())) {
            return Optional.of(com.codenear.butterfly.payment.domain.CardInfo.kakaoPaymentBuilder()
                    .approveResponseDTO(this)
                    .buildKakaoPayment());
        }
        return Optional.empty();
    }

    @Getter
    public static class Amount {
        private Integer total; // 총 결제 금액
        private Integer tax_free; // 비과세 금액
        private Integer vat; // 부가세 금액
        private Integer point; // 사용한 포인트 금액
        private Integer discount; // 할인 금액
    }

    @Getter
    public static class CardInfo {
        private String kakaopay_purchase_corp;
        private String kakaopay_purchase_corp_code;
        private String kakaopay_issuer_corp;
        private String kakaopay_issuer_corp_code;
        private String bin;
        private String card_type;
        private String install_month;
        private String approved_id;
        private String card_mid;
        private String interest_free_install;
        private String installment_type;
        private String card_item_code;
    }
}
