package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Product", description = "상품 정보 API")
public interface ProductControllerSwagger {

    @Operation(summary = "상품 정보", description = "**[메인 / 카테고리]** 화면에 포함된 상품 정보")
    ResponseEntity<ResponseDTO> productInfo();
}
