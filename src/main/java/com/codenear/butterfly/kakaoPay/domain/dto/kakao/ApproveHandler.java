package com.codenear.butterfly.kakaoPay.domain.dto.kakao;

import com.codenear.butterfly.kakaoPay.domain.Amount;
import com.codenear.butterfly.kakaoPay.domain.CardInfo;
import com.codenear.butterfly.kakaoPay.domain.SinglePayment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class ApproveHandler {
    protected String orderId;
    protected String productName;
    protected int quantity;
    protected int point;

    public abstract SinglePayment createSinglePayment();

    public abstract Amount createAmount();

    public abstract Object getOrderDetailDto();

    public Optional<CardInfo> createCardInfo() {
        return Optional.empty();
    }

}
