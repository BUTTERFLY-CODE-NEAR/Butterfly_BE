package com.codenear.butterfly.payment.kakaoPay.domain.dto.handler;

import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.CardInfo;
import com.codenear.butterfly.payment.domain.SinglePayment;
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
