package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.application.CredentialService;
import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.VerifyFindPasswordRequestDTO;
import com.codenear.butterfly.member.presentation.swagger.CredentialControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/credential")
public class CredentialController implements CredentialControllerSwagger{

    private final CredentialService credentialService;

    @PostMapping("/email/{phoneNumber}")
    public ResponseEntity<ResponseDTO> sendCodeForEmailVerification(@PathVariable(name = "phoneNumber") String phoneNumber) {
        credentialService.sendFindEmailCode(phoneNumber);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/email")
    public ResponseEntity<ResponseDTO> findEmail(@RequestBody @Valid CertifyRequest request) {
        String email = credentialService.findEmail(request);
        return ResponseUtil.createSuccessResponse(email);
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseDTO> sendPasswordResetCode(@RequestBody @Valid FindPasswordRequestDTO request) {
        credentialService.sendFindPasswordCode(request);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<ResponseDTO> verifyPasswordResetCode(@RequestBody @Valid VerifyFindPasswordRequestDTO request) {
        credentialService.verifyFindPasswordCode(request);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        credentialService.resetPassword(request);
        return ResponseUtil.createSuccessResponse(null);
    }
}
