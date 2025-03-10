package com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq;

public record InventoryDecreaseMessageDTO(String productName,
                                          int quantity) {
}
