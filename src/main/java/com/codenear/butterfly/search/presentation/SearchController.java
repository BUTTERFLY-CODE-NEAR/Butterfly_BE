package com.codenear.butterfly.search.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.search.application.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController implements SearchControllerSwagger {
    private final SearchService searchService;

    @GetMapping("/related")
    public ResponseEntity<ResponseDTO> getRelatedKeywords(@RequestParam String keyword) {
        return ResponseUtil.createSuccessResponse(searchService.getRelatedKeywords(keyword));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getSearchLogList(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(searchService.getSearchList(memberDTO));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> deleteAllSearchLog(@AuthenticationPrincipal MemberDTO memberDTO) {
        searchService.deleteAllSearchLog(memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }

    @DeleteMapping("/{keyword}")
    public ResponseEntity<ResponseDTO> deleteSearchLog(@PathVariable("keyword") String keyword, @AuthenticationPrincipal MemberDTO memberDTO) {
        searchService.deleteSearchLog(keyword, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }

    @GetMapping("/{keyword}")
    public ResponseEntity<ResponseDTO> search(@PathVariable("keyword") String keyword, @AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(searchService.search(keyword, memberDTO));
    }
}
