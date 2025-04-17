package com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq;

import lombok.Builder;

public class InventoryIncreaseMessageDTO extends InventoryMessage {

    @Builder
    public InventoryIncreaseMessageDTO(String productName, int quantity) {
        super(productName, quantity);
    }
}
