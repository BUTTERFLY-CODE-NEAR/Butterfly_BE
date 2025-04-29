package com.codenear.butterfly.payment.domain.dto.rabbitmq;

import lombok.Builder;

public class InventoryDecreaseMessageDTO extends InventoryMessage {

    @Builder
    public InventoryDecreaseMessageDTO(String productName, int quantity) {
        super(productName, quantity);
    }

}
