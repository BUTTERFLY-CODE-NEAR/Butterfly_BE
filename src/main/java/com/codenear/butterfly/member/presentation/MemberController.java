package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.presentation.swagger.MemberControllerSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController implements MemberControllerSwagger {
    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<ResponseDTO> memberInfo(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(memberService.getMemberInfo(memberDTO));
    }
}
