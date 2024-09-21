package com.codenear.butterfly.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "닉네임 생성 JSON", description = "닉네임 생성 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record NicknameDTO(
        @Schema(description = "닉네임") String nickname) {
}