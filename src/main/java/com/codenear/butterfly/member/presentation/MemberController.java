package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.VerifyFindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.enums.VerificationType;
import com.codenear.butterfly.member.presentation.swagger.MemberControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController implements MemberControllerSwagger {
    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<ResponseDTO> memberInfo(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(memberService.getMemberInfo(memberDTO));
    }

    @PostMapping("/credential/email/{phoneNumber}")
    public ResponseEntity<ResponseDTO> sendCodeForEmailVerification(@PathVariable String phoneNumber) {
        memberService.sendFindEmailcode(phoneNumber);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/credential/email")
    public ResponseEntity<ResponseDTO> findEmail(@RequestBody @Valid CertifyRequest request) {
        String email = memberService.findEmail(request);
        return ResponseUtil.createSuccessResponse(email);
    }

    @PostMapping("/credential/password")
    public ResponseEntity<ResponseDTO> sendPwResetCode(@RequestBody @Valid FindPasswordRequestDTO request){
        memberService.sendFindPasswordCode(request);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/credential/password/verify")
    public ResponseEntity<ResponseDTO> verifyPasswordResetCode(@RequestBody @Valid VerifyFindPasswordRequestDTO request) {
        memberService.verifyFindPasswordCode(request);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/credential/password/reset")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        memberService.resetPassword(request);
        return ResponseUtil.createSuccessResponse(null);
    }

}
