package com.codenear.butterfly.payment.kakaoPay.domain;

import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.CancelResponseDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("KAKAO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoPaymentCancel extends CancelPayment {
    private String aid; // 요청 고유 번호
    private String cid; // 가맹점 코드
    private String itemCode; // 상품 코드
    private String payload; // 결제 승인 요청에 대해 저장 값, 요청 시 전달된 내용

    @Builder(builderMethodName = "kakaoPaymentBuilder", buildMethodName = "buildKakaoPayment")
    public KakaoPaymentCancel(CancelResponseDTO cancelResponseDTO) {
        super(cancelResponseDTO);
        this.aid = cancelResponseDTO.getAid();
        this.cid = cancelResponseDTO.getCid();
        this.itemCode = cancelResponseDTO.getItem_code();
        this.payload = cancelResponseDTO.getPayload();
    }
}
