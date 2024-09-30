package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.product.application.CategoryService;
import com.codenear.butterfly.product.application.FavoriteService;
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
    private final FavoriteService favoriteService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseDTO> categoryInfo() {
        return ResponseUtil.createSuccessResponse(categoryService.getCategories());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> productInfoByCategory(@RequestParam(value = "category", required = false) String category,
                                                             @AuthenticationPrincipal Member member) {
        if (category == null || category.isEmpty()) {
            return ResponseUtil.createSuccessResponse(productViewService.getAllProducts(member));
        } else {
            return ResponseUtil.createSuccessResponse(productViewService.getProductsByCategory(category, member));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO> productDetail(@PathVariable(value = "productId") Long productId,
                                                     @AuthenticationPrincipal Member member) {
        return ResponseUtil.createSuccessResponse(productViewService.getProductDetail(productId, member));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ResponseDTO> getFavorites(@AuthenticationPrincipal Member member) {
        List<Long> favorites = favoriteService.getFavoriteAll(member);

        if (favorites.isEmpty()) {
            return ResponseUtil.createSuccessResponse(HttpStatus.NO_CONTENT, "찜 목록이 비어있습니다.", null);
        }

        return ResponseUtil.createSuccessResponse(favorites);
    }

    @GetMapping("/favorites/{productId}")
    public ResponseEntity<ResponseDTO> isFavorite(@PathVariable(value = "productId") Long productId,
                                                  @AuthenticationPrincipal Member member) {
        return ResponseUtil.createSuccessResponse(productViewService.isProductFavorite(member, productId));
    }

    @PostMapping("/favorites/{productId}")
    public ResponseEntity<ResponseDTO> addFavorite(@PathVariable(value = "productId") Long productId,
                                                   @AuthenticationPrincipal Member member) {
        favoriteService.addFavorite(member, productId);
        return ResponseUtil.createSuccessResponse(null);
    }

    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<ResponseDTO> removeFavorite(@PathVariable(value = "productId") Long productId,
                                                      @AuthenticationPrincipal Member member) {
        favoriteService.removeFavorite(member, productId);
        return ResponseUtil.createSuccessResponse(null);
    }
}