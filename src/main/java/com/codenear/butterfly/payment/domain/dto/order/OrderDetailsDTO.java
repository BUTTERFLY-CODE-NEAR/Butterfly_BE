package com.codenear.butterfly.payment.domain.dto.order;

import com.codenear.butterfly.payment.domain.dto.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 내역 JSON", description = "주문 내역 정보 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record OrderDetailsDTO(
        @Schema(description = "주문 번호") String orderCode,
        @Schema(description = "주문 날짜") String orderDate,
        @Schema(description = "상품 이름") String productName,
        @Schema(description = "옵션 이름") String optionName,
        @Schema(description = "상품 이미지", example = "http://example.com/profile.jpg") String productImage,
        @Schema(description = "총 결제 금액") Integer total,
        @Schema(description = "상품 수량") Integer quantity,
        @Schema(description = "주문 상태", example = "배송 중") OrderStatus orderStatus,
        @Schema(description = "결제 아이디") String tid
) {
}
