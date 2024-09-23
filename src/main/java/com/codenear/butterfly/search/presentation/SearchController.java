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
public class SearchController {
    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<ResponseDTO> addSearchLog(@RequestParam String keyword, @AuthenticationPrincipal Member member) {
        searchService.addSearchLog(keyword, member);
        return ResponseUtil.createSuccessResponse(null);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getSearchLogList(@AuthenticationPrincipal Member member) {
        return ResponseUtil.createSuccessResponse(searchService.getSearchList(member));
    }
}
