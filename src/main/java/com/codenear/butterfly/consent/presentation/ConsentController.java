package com.codenear.butterfly.consent.presentation;

import com.codenear.butterfly.consent.application.ConsentService;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consent")
@RequiredArgsConstructor
public class ConsentController implements ConsentControllerSwagger {
    private final ConsentService consentService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getConsents(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(consentService.getConsentsInfo(memberDTO));
    }
}
