package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.product.application.CategoryService;
import com.codenear.butterfly.product.application.FavoriteService;
import com.codenear.butterfly.product.application.ProductViewService;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController implements ProductControllerSwagger {

    private final CategoryService categoryService;
    private final ProductViewService productViewService;
    private final FavoriteService favoriteService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseDTO> categoryInfo() {
        return ResponseUtil.createSuccessResponse(categoryService.getCategories());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> productInfoByCategory(@RequestParam(value = "category", required = false) String category,
                                                             @AuthenticationPrincipal MemberDTO memberDTO) {
        if (category == null || category.isEmpty()) {
            return ResponseUtil.createSuccessResponse(productViewService.getAllProducts(memberDTO));
        } else {
            return ResponseUtil.createSuccessResponse(productViewService.getProductsByCategory(category, memberDTO));
        }
    }

    @GetMapping("/sb")
    public ResponseEntity<ResponseDTO> getSmallBusinessProducts(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(productViewService.getSmallBusinessProducts(memberDTO));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO> productDetail(@PathVariable(value = "productId") Long productId,
                                                     @AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(productViewService.getProductDetail(productId, memberDTO));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ResponseDTO> getFavorites(@AuthenticationPrincipal MemberDTO memberDTO) {
        List<ProductViewDTO> favorites = favoriteService.getFavoriteAll(memberDTO.getId());

        return ResponseUtil.createSuccessResponse(favorites);
    }

    @GetMapping("/favorites/{productId}")
    public ResponseEntity<ResponseDTO> isFavorite(@PathVariable(value = "productId") Long productId,
                                                  @AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(productViewService.isProductFavorite(memberDTO, productId));
    }

    @PostMapping("/favorites/{productId}")
    public ResponseEntity<ResponseDTO> addFavorite(@PathVariable(value = "productId") Long productId,
                                                   @AuthenticationPrincipal MemberDTO memberDTO) {
        favoriteService.addFavorite(memberDTO, productId);
        return ResponseUtil.createSuccessResponse(null);
    }

    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<ResponseDTO> removeFavorite(@PathVariable(value = "productId") Long productId,
                                                      @AuthenticationPrincipal MemberDTO memberDTO) {
        boolean isRemoved = favoriteService.removeFavorite(memberDTO, productId);
        return ResponseUtil.createSuccessResponse(isRemoved);
    }
}