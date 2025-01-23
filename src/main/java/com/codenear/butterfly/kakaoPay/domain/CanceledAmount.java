package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class CanceledAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer total; // 총 결제 금액
    private Integer taxFree; // 비과세 금액
    private Integer vat; // 부가세 금액
    private Integer point; // 사용한 포인트 금액
    private Integer discount; // 할인 금액

    @OneToOne(mappedBy = "canceledAmount")
    private CancelPayment cancelPayment;

    @Builder
    public CanceledAmount(CancelResponseDTO cancelResponseDTO) {
        this.total = cancelResponseDTO.getAmount().getTotal();
        this.taxFree = cancelResponseDTO.getAmount().getTax_free();
        this.vat = cancelResponseDTO.getAmount().getVat();
        this.point = cancelResponseDTO.getAmount().getPoint();
        this.discount = cancelResponseDTO.getAmount().getDiscount();
    }
}
