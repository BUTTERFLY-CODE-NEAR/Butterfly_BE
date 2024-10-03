package com.codenear.butterfly.search.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Search", description = "**(최근, 연관) 검색어  API**")
public interface SearchControllerSwagger {

    @Operation(summary = "연관 검색어 정보", description = "연관 검색어 정보 API")
    public ResponseEntity<ResponseDTO> getRelatedKeywords(@RequestParam String keyword);

    @Operation(summary = "최근 검색어 정보", description = "최근 검색어 목록 정보 API")
    public ResponseEntity<ResponseDTO> getSearchLogList(@AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "최근 검색어 전체 삭제", description = "최근 검체어 목록 전체 삭제 API")
    public ResponseEntity<ResponseDTO> deleteAllSearchLog(@AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "최근 검색어 선택 삭제", description = "최근 검색어 목록 선택 삭제 API")
    public ResponseEntity<ResponseDTO> deleteSearchLog(@PathVariable("keyword") String keyword, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "테스트 검색 데이터 추가", description = "테스트 검색 데이터 추가 API")
    public ResponseEntity<ResponseDTO> testSearchLog(@RequestParam String keyword, @AuthenticationPrincipal MemberDTO memberDTO);

}
