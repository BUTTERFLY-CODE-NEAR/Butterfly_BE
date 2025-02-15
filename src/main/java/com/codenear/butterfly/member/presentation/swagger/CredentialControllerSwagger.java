package com.codenear.butterfly.member.presentation.swagger;

import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.MemberInfoDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.VerifyFindPasswordRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 이메일 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"code\": 200, \"message\": \"\", \"body\": \"example@gmail.com\" }")
                            }
                    )),
    })
    @RequestBody(content = @Content(
            mediaType = "application/json",
            examples = {
                    @ExampleObject(
                            name = "PHONE 예시", value = "{ \"phoneNumber\": \"01012345678\", \"certifyCode\": \"123456\" }"),
                    @ExampleObject(
                            name = "EMAIL 예시", value = "{ \"email\": \"example@gmail.com\", \"certifyCode\": \"123456\" }")
            }
    ))
    ResponseEntity<ResponseDTO> findEmail(@RequestBody @Valid CertifyRequest request);

    @Operation(summary = "비밀번호 재설정 코드 전송", description = "비밀번호 재설정 인증 코드 전송 API")
    @RequestBody(content = @Content(
            mediaType = "application/json",
            examples = {
                    @ExampleObject(
                            name = "PHONE 예시", value = "{ \"identifier\": \"01012345678\", \"type\": \"PHONE\" }"),
                    @ExampleObject(
                            name = "EMAIL 예시", value = "{ \"identifier\": \"example@gmail.com\", \"type\": \"EMAIL\" }")
            }
    ))
    ResponseEntity<ResponseDTO> sendPasswordResetCode(@RequestBody @Valid FindPasswordRequestDTO request);

    @Operation(summary = "비밀번호 재설정 코드 검증", description = "인증 코드 검증 후 비밀번호 재설정 가능 여부 확인 API")
    @RequestBody(content = @Content(
            mediaType = "application/json",
            examples = {
                    @ExampleObject(
                            name = "PHONE 예시",
                            value = "{ \"identifier\": \"01012345678\", \"type\": \"PHONE\", \"certifyCode\": \"123456\" }"),
                    @ExampleObject(
                            name = "EMAIL 예시",
                            value = "{ \"identifier\": \"example@gmail.com\", \"type\": \"EMAIL\" , \"certifyCode\": \"123456\" }")
            }
    ))
    ResponseEntity<ResponseDTO> verifyPasswordResetCode(@RequestBody @Valid VerifyFindPasswordRequestDTO request);

    @Operation(summary = "비밀번호 재설정", description = "인증된 유저 비밀번호 재설정 API")
    @RequestBody(content = @Content(
            mediaType = "application/json",
            examples = {
                    @ExampleObject(
                            name = "PHONE 예시",
                            value = "{ \"identifier\": \"01012345678\", \"type\": \"PHONE\", \"newPassword\": \"1q2w3e4r!!!\", \"platform\": \"platform\" }"),
                    @ExampleObject(
                            name = "EMAIL 예시",
                            value = "{ \"identifier\": \"example@gmail.com\", \"type\": \"EMAIL\" , \"newPassword\": \"1q2w3e4r!!!\", \"platform\": \"platform\" }")
            }
    ))
    ResponseEntity<ResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request);
}
