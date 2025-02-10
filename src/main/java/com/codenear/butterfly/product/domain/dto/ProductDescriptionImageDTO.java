package com.codenear.butterfly.product.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductDescriptionImageDTO(
        @Schema(description = "상품 설명 이미지가 저장된 s3주소", example = "https://codenear.kr/~") String imageUrl
) {
}
