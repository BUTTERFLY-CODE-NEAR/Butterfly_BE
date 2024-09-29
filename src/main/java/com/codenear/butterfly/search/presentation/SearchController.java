package com.codenear.butterfly.search.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.search.application.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseDTO> getSearchLogList(@AuthenticationPrincipal Member member) {
        return ResponseUtil.createSuccessResponse(searchService.getSearchList(member));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> deleteAllSearchLog(@AuthenticationPrincipal Member member) {
        searchService.deleteAllSearchLog(member);
        return ResponseUtil.createSuccessResponse(null);
    }

    @DeleteMapping("/{keyword}")
    public ResponseEntity<ResponseDTO> deleteSearchLog(@PathVariable("keyword") String keyword, @AuthenticationPrincipal Member member) {
        searchService.deleteSearchLog(keyword, member);
        return ResponseUtil.createSuccessResponse(null);
    }

    @GetMapping("/test")
    public ResponseEntity<ResponseDTO> testSearchLog(@RequestParam String keyword, @AuthenticationPrincipal Member member) {
        searchService.addSearchLog(keyword, member);
        return ResponseUtil.createSuccessResponse(null);
    }
}
