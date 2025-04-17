package com.codenear.butterfly.kakaoPay.domain.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderDTO(@Schema(description = "주문 할 상품명") String productName,
                       @Schema(description = "상품 주문 개수") int orderQuantity) {
}
