package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.product.application.CategoryService;
import com.codenear.butterfly.product.application.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}