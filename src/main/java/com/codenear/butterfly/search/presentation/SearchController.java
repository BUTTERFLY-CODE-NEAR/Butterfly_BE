package com.codenear.butterfly.search.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.search.application.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/search/log")
    public ResponseEntity<ResponseDTO> addSearchLog(@RequestParam String keyword, @AuthenticationPrincipal Member member) {
        searchService.addSearchLog(keyword, member);
        return ResponseUtil.createSuccessResponse(null);
    }

    @GetMapping("/search/list")
    public ResponseEntity<ResponseDTO> getSearchList(@AuthenticationPrincipal Member member) {
        return ResponseUtil.createSuccessResponse(searchService.getSearchList(member));
    }
}
