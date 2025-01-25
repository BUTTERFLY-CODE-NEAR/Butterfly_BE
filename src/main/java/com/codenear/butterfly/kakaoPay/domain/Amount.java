package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor
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

    @Builder
    public Amount (ApproveResponseDTO approveResponseDTO) {
        this.total = Objects.requireNonNull(approveResponseDTO).getAmount().getTotal();
        this.taxFree = approveResponseDTO.getAmount().getTax_free();
        this.vat = approveResponseDTO.getAmount().getVat();
        this.point = approveResponseDTO.getAmount().getPoint();
        this.discount = approveResponseDTO.getAmount().getDiscount();
    }
}