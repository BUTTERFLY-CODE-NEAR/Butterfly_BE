package com.codenear.butterfly.certify.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "휴대폰 인증 JSON", description = "휴대폰 인증 시 JSON 데이터 입니다.")
@Getter
public class CertifyRequestDTO {
    @Schema(description = "휴대폰 번호", example = "01012345678")
    @Size(min = 11, max = 11, message = "번호는 '-'을 제외한 11자리만 입력해 주세요.")
    @NotNull
    private String phoneNumber;

    @Schema(description = "인증 번호")
    @NotNull
    private String certifyCode;
}
