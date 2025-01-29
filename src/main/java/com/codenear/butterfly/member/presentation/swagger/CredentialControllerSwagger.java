package com.codenear.butterfly.member.presentation.swagger;

import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.VerifyFindPasswordRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Credential", description = "**아이디/비밀번호 찾기 API**")
public interface CredentialControllerSwagger {

    @Operation(summary = "이메일 찾기 인증 코드 전송", description = "핸드폰 번호 입력 후 이메일 인증 코드 전송 API")
    @Parameters({
            @Parameter(name = "phoneNumber", description = "유저 전화번호", required = true)
    })
    ResponseEntity<ResponseDTO> sendCodeForEmailVerification(@PathVariable(name = "phoneNumber") String phoneNumber);

    @Operation(summary = "이메일 찾기", description = "인증 코드 검증 후 유저 이메일 반환 API")
    @RequestBody(content = @Content(schema = @Schema(implementation = CertifyRequest.class)))
    ResponseEntity<ResponseDTO> findEmail(@RequestBody @Valid CertifyRequest request);

    @Operation(summary = "비밀번호 재설정 코드 전송", description = "비밀번호 재설정 인증 코드를 전송 API")
    @RequestBody(content = @Content(schema = @Schema(implementation = FindPasswordRequestDTO.class)))
    ResponseEntity<ResponseDTO> sendPasswordResetCode(@RequestBody @Valid FindPasswordRequestDTO request);

    @Operation(summary = "비밀번호 재설정 코드 검증", description = "인증 코드 검증 후 비밀번호 재설정 가능 여부 확인 API")
    @RequestBody(content = @Content(schema = @Schema(implementation = VerifyFindPasswordRequestDTO.class)))
    ResponseEntity<ResponseDTO> verifyPasswordResetCode(@RequestBody @Valid VerifyFindPasswordRequestDTO request);

    @Operation(summary = "비밀번호 재설정", description = "인증된 유저 비밀번호 재설정 API")
    @RequestBody(content = @Content(schema = @Schema(implementation = ResetPasswordRequestDTO.class)))
    ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request);
}
