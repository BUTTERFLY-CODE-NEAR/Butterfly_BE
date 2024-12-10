package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.application.PhoneRegistrationService;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.presentation.swagger.PhoneRegistrationControllerSwagger;
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
@RequestMapping("/member/phone-registration")
@RequiredArgsConstructor
public class PhoneRegistrationController implements PhoneRegistrationControllerSwagger {

    private final PhoneRegistrationService phoneRegistrationService;

    @PostMapping("/{phoneNumber}")
    public ResponseEntity<ResponseDTO> sendRegistrationCode(@PathVariable String phoneNumber) {
        phoneRegistrationService.sendRegistrationCode(phoneNumber);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> checkCertifyCode(@Valid @RequestBody CertifyRequest request,
                                                        @AuthenticationPrincipal MemberDTO loginMember) {
        phoneRegistrationService.checkRegistrationCode(request, loginMember);
        return ResponseUtil.createSuccessResponse(null);
    }
}
