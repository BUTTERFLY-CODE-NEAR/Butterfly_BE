package com.codenear.butterfly.certify.presentation;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.certify.domain.dto.CertifyRequestDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certify")
public class CertifyController implements CertifyControllerSwagger {
    private final CertifyService certifyService;

    @PostMapping("/{phoneNumber}")
    public ResponseEntity<ResponseDTO> sendCertifyCode(@PathVariable String phoneNumber) {
        certifyService.sendCertifyCode(phoneNumber);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> checkCertifyCode(@Valid @RequestBody CertifyRequestDTO certifyValidRequestDTO,
                                                        @AuthenticationPrincipal MemberDTO memberDTO) {
        certifyService.checkCertifyCode(certifyValidRequestDTO, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }
}
