package com.codenear.butterfly.payment.domain;

import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CardInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String purchaseCorp; // 매입사명
    private String purchaseCorpCode; // 매입사 코드
    private String issuerCorp; // 발급사명
    private String issuerCorpCode; // 발급사 코드
    private String bin; // 카드 BIN
    private String cardType; // 카드 타입
    private String installMonth; // 할부 개월 수
    private String approvedId; // 카드사 승인번호
    private String cardMid; // 카드사 가맹점 번호
    private String interestFreeInstall; // 무이자할부 여부(Y/N)
    private String installmentType; // 할부 유형
    private String cardItemCode; // 카드 상품 코드

    @OneToOne(mappedBy = "cardInfo")
    private SinglePayment singlePayment;

    @Builder(builderMethodName = "kakaoPaymentBuilder", buildMethodName = "buildKakaoPayment")
    public CardInfo(ApproveResponseDTO approveResponseDTO) {
        this.approvedId = approveResponseDTO.getCard_info().getApproved_id();
        this.bin = approveResponseDTO.getCard_info().getBin();
        this.cardMid = approveResponseDTO.getCard_info().getCard_mid();
        this.cardType = approveResponseDTO.getCard_info().getCard_type();
        this.installMonth = approveResponseDTO.getCard_info().getInstall_month();
        this.cardItemCode = approveResponseDTO.getCard_info().getCard_item_code();
        this.installmentType = approveResponseDTO.getCard_info().getInstallment_type();
        this.interestFreeInstall = approveResponseDTO.getCard_info().getInterest_free_install();
        this.purchaseCorp = approveResponseDTO.getCard_info().getKakaopay_purchase_corp();
        this.purchaseCorpCode = approveResponseDTO.getCard_info().getKakaopay_purchase_corp_code();
        this.issuerCorp = approveResponseDTO.getCard_info().getKakaopay_issuer_corp();
        this.issuerCorpCode = approveResponseDTO.getCard_info().getKakaopay_issuer_corp_code();
    }

    @Builder(builderMethodName = "tossPaymentBuilder", buildMethodName = "buildTossPayment")
    public CardInfo(ConfirmResponseDTO confirmResponseDTO) {
        ConfirmResponseDTO.Card card = confirmResponseDTO.getCard();

        this.approvedId = card.getApproveNo();
        this.bin = card.getNumber();
        this.cardType = card.getCardType();
        this.installMonth = String.valueOf(card.getInstallmentPlanMonths());
        this.interestFreeInstall = card.getInstallmentPlanMonths() == 0 ? "Y" : "N";
        this.issuerCorpCode = card.getIssuerCode();
        this.purchaseCorpCode = card.getAcquirerCode();
    }
}