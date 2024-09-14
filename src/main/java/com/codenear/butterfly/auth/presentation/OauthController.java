package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.OauthService;
import com.codenear.butterfly.auth.domain.dto.OauthDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {
    private final OauthService oauthService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody OauthDTO oauthDTO, HttpServletResponse response) {
        oauthService.socialLoginAndIssueJwt(oauthDTO, response);
        return ResponseUtil.createSuccessResponse(null);
    }
}
