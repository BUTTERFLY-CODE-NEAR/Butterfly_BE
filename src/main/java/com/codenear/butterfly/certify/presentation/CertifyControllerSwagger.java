package com.codenear.butterfly.certify.presentation;

import com.codenear.butterfly.certify.domain.dto.CertifyRequestDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Certify", description = "**휴대폰 인증 API**")
public interface CertifyControllerSwagger {

    @Operation(summary = "휴대폰 인증 번호 전송", description = "휴대폰 인증 번호 전송 API")
    ResponseEntity<ResponseDTO> sendCertifyCode(@PathVariable String phoneNumber);

    @Operation(summary = "휴대폰 인증 번호 검증", description = "휴대폰 인증 번호 검증 API")
    ResponseEntity<ResponseDTO> checkCertifyCode(@RequestBody CertifyRequestDTO certifyValidRequestDTO, @AuthenticationPrincipal MemberDTO memberDTO);
}
