package com.codenear.butterfly.kakaoPay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CardInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String kakaopayPurchaseCorp; // 카카오페이 매입사명
    private String kakaopayPurchaseCorpCode; // 카카오페이 매입사 코드
    private String kakaopayIssuerCorp; // 카카오페이 발급사명
    private String kakaopayIssuerCorpCode; // 카카오페이 발급사 코드
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
}