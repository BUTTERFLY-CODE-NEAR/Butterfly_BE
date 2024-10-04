package com.codenear.butterfly.product.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.dto.ProductDetailDTO;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Product", description = "**상품 정보 API**")
public interface ProductControllerSwagger {

    @Operation(summary = "카테고리", description = "카테고리 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> categoryInfo();

    @Operation(summary = "(카테고리별) 상품 정보", description = "[카테고리] (카테고리별) 상품 정보 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = ProductViewDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> productInfoByCategory(@RequestParam("category") String category,
                                                      @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "상품 상세 정보", description = "상품 상세 정보 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = ProductDetailDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> productDetail(@PathVariable(value = "productId") Long productId,
                                              @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "찜 목록 조회", description = "찜 목록 조회 Api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getFavorites(@AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "찜 여부 확인", description = "찜 여부 확인 Api")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> isFavorite(@PathVariable(value = "productId") Long productId,
                                           @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "찜 목록 등록", description = "찜 목록 추가 Api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "409", description = "Conflict (Duplicate)")
    })
    ResponseEntity<ResponseDTO> addFavorite(@PathVariable(value = "productId") Long productId,
                                                   @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "찜 목록 삭제", description = "찜 목록 삭제 Api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    ResponseEntity<ResponseDTO> removeFavorite(@PathVariable(value = "productId") Long productId,
                                            @AuthenticationPrincipal MemberDTO memberDTO);
}
