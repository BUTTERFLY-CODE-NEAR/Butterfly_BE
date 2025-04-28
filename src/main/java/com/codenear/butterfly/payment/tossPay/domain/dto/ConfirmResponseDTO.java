package com.codenear.butterfly.payment.tossPay.domain.dto;

import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.CardInfo;
import com.codenear.butterfly.payment.domain.PaymentApproval;
import com.codenear.butterfly.payment.domain.PaymentMethod;
import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.tossPay.domain.TossPayment;
import com.codenear.butterfly.payment.tossPay.domain.TossPaymentType;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
public class ConfirmResponseDTO implements PaymentApproval {
    private String mId; // 상점아이디 (토스에서 발급)
    private String lastTransactionKey; // 마지막 거래의 키값
    private String paymentKey; // 결제의 키값 (식별자)
    private String orderId; // 주문번호
    private String orderName; // 상품명
    private String status; // 결제 상태
    private String requestedAt; // 결제가 일어난 시간
    private String approvedAt; // 결제가 승인된 시간
    private TossPaymentType type; // 결제 타입 정보 (NOMAL(일반결제), BILLING (자동결제), BRANDPAY (브랜드 페이)
    private int totalAmount; // 총 금액
    private int balanceAmount; // 취소할 수 있는 금액 (잔고)
    private int suppliedAmount; // 공급가액
    private int vat; // 부가세
    private int taxFreeAmount; // 면세금액
    private String method; // 결제수단 (카드, 가상계좌, 간편결제, 휴대폰, 계좌이체, 문화상품권, 도서문화상품권, 게임문화상품권)
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달 내용
    @Setter
    private int quantity;
    private EasyPay easyPay;
    private Card card;

    @Override
    public String getProductName() {
        return orderName;
    }

    @Override
    public String getPaymentMethod() {
        if (method.equals(PaymentMethod.카드.name())) {
            return "CARD";
        }
        return method;
    }

    @Override
    public SinglePayment toSinglePayment(Long memberId) {
        return TossPayment.tossPaymentBuilder()
                .confirmResponseDTO(this)
                .memberId(memberId)
                .buildTossPayment();
    }

    @Override
    public Amount toAmount() {
        return Amount.tossPaymentBuilder()
                .confirmResponseDTO(this)
                .buildTossPayment();
    }

    @Override
    public Optional<CardInfo> toCardInfo() {
        if (this.getMethod().equals(PaymentMethod.카드.name())) {
            return Optional.ofNullable(CardInfo.tossPaymentBuilder()
                    .confirmResponseDTO(this)
                    .buildTossPayment());
        }
        return Optional.empty();
    }

    @Getter
    public class Card {
        private String issuerCode; // 카드 발급사 코드 (두자리)
        private String acquirerCode; // 카드 매입사 코드 (두자리)
        private String number; // 카드번호
        private int installmentPlanMonths; // 할부 개월 수
        private String approveNo; // 카드사 승인 번호
        private boolean useCardPoint; // 카드사 포인트 사용 여부
        private String cardType; // 카드 종류 (신용, 체크, 기프트, 미확인)
        private String ownerType; // 카드 소유자 타입 (개인, 법인, 미확인)
        private String receiptUrl; // 발행 영수증 정보(URL)
        private int amount; // 카드사에 요청한 금액
    }

    @Getter
    public class EasyPay {
        private String provider;
        private int amount;
        private int discountAmount;
    }

}
