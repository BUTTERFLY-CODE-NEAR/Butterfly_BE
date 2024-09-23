package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.product.application.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController implements ProductControllerSwagger {

    private final ProductViewService productViewService;

    @GetMapping
    public ResponseEntity<ResponseDTO> productInfo() {
        return ResponseUtil.createSuccessResponse(productViewService.getAllProducts());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseDTO> productInfoByCategory(@PathVariable("category") String category) {
        return ResponseUtil.createSuccessResponse(productViewService.getProductsByCategory(category));
    }
}
