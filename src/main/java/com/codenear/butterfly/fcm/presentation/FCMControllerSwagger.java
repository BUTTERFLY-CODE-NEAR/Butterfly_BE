package com.codenear.butterfly.fcm.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "FCM", description = "**FCM API**")
public interface FCMControllerSwagger {

    @Operation(summary = "토큰 등록", description = "토큰 등록 API (로그인 과정에 **필수 등록**)")
    ResponseEntity<ResponseDTO> registerFCM(@PathVariable String token, @AuthenticationPrincipal MemberDTO memberDTO);
}
