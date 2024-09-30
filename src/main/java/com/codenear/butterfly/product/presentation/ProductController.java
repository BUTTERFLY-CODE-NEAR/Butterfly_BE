package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.product.application.CategoryService;
import com.codenear.butterfly.product.application.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController implements ProductControllerSwagger {

    private final CategoryService categoryService;
    private final ProductViewService productViewService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseDTO> categoryInfo() {
        return ResponseUtil.createSuccessResponse(categoryService.getCategories());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> productInfoByCategory(@RequestParam(value = "category", required = false) String category) {
        if (category == null || category.isEmpty()) {
            return ResponseUtil.createSuccessResponse(productViewService.getAllProducts());
        } else {
            return ResponseUtil.createSuccessResponse(productViewService.getProductsByCategory(category));
        }
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO> productDetail(@PathVariable(value = "productId") Long productId,
                                                     @AuthenticationPrincipal Member member) {
        return ResponseUtil.createSuccessResponse(productViewService.getProductDetail(productId, member));
    }
}