package com.codenear.butterfly.fcm.presentation;

import com.codenear.butterfly.fcm.application.FCMFacade;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/FCM")
public class FCMController {

    private final FCMFacade fcmFacade;

    @PostMapping("/{token}")
    public ResponseEntity<ResponseDTO> registerFcm(@PathVariable String token,
                                                   @AuthenticationPrincipal MemberDTO memberDTO) {
        fcmFacade.saveFCM(token, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }
}
