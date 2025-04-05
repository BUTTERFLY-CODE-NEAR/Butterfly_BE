package com.codenear.butterfly.payment.domain.dto.rabbitmq;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class InventoryMessage {
    private String productName;
    private int quantity;

    public InventoryMessage(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
}
