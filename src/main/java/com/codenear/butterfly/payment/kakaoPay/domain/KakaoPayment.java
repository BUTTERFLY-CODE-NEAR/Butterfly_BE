package com.codenear.butterfly.payment.kakaoPay.domain;

import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("KAKAO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoPayment extends SinglePayment {
    private String aid; // 요청 고유 번호
    private String cid; // 가맹점 코드
    private String sid; // 정기 결제용 ID

    @Builder(builderMethodName = "kakaoPaymentBuilder", buildMethodName = "buildKakaoPayment")
    public KakaoPayment(ApproveResponseDTO approveResponseDTO, Long memberId) {
        super(approveResponseDTO, memberId);
        this.aid = approveResponseDTO.getAid();
        this.cid = approveResponseDTO.getCid();
        this.sid = approveResponseDTO.getSid();
    }
}
