package com.codenear.butterfly.payment.domain.dto.handler;

import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.OrderDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class CancelHandler {
    protected OrderDetails orderDetails;

    public abstract CancelPayment createCancelPayment();

    public int getRestorePoint() {
        return orderDetails.getPoint();
    }

    public String getProductName() {
        return orderDetails.getProductName();
    }

    public int getQuantity() {
        return orderDetails.getQuantity();
    }
}

