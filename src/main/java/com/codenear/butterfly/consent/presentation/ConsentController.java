package com.codenear.butterfly.consent.presentation;

import com.codenear.butterfly.consent.application.ConsentService;
import com.codenear.butterfly.consent.application.ConsentUpdateService;
import com.codenear.butterfly.consent.dto.ConsentUpdateRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consent")
@RequiredArgsConstructor
public class ConsentController implements ConsentControllerSwagger {

    private final ConsentService consentService;
    private final ConsentUpdateService consentUpdateService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getConsents(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(consentService.getConsentsInfo(memberDTO));
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO> updateConsent(@Valid @RequestBody ConsentUpdateRequest updateRequestDTO, @AuthenticationPrincipal MemberDTO memberDTO) {
        consentUpdateService.updateConsent(updateRequestDTO, memberDTO.getId());
        return ResponseUtil.createSuccessResponse(null);
    }
}
