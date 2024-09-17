package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Member", description = "유저 정보 API")
public interface MemberControllerSwagger {

    @Operation(summary = "유저 정보", description = "**[메인 / 마이페이지]** 화면에 포함된 유저 정보")
    ResponseEntity<ResponseDTO> memberInfo(@AuthenticationPrincipal Member member);
}
