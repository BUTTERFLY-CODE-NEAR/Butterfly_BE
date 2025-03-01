package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.presentation.swagger.LogoutAndWithdrawControllerSwagger;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.codenear.butterfly.global.util.ResponseUtil.createSuccessResponse;

@RestController
@RequiredArgsConstructor
public class LogoutAndWithdrawController implements LogoutAndWithdrawControllerSwagger {
    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout() {
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ResponseDTO> withdraw(@AuthenticationPrincipal MemberDTO loginMember, HttpServletResponse response) {
        authService.handleWithdraw(loginMember, response);
        return createSuccessResponse(null);
    }
}
