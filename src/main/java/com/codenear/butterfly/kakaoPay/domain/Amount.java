package com.codenear.butterfly.kakaoPay.domain;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
public class Amount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer total; // 총 결제 금액
    private Integer taxFree; // 비과세 금액
    private Integer vat; // 부가세 금액
    private Integer point; // 사용한 포인트 금액
    private Integer discount; // 할인 금액

    @OneToOne(mappedBy = "amount")
    private SinglePayment singlePayment;
}