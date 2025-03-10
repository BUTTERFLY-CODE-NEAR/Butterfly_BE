package com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq;

public record InventoryIncreaseMessageDTO(String productName,
                                          int quantity) {
}
