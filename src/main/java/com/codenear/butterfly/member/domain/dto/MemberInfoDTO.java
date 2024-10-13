package com.codenear.butterfly.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "유저 정보 JSON", description = "유저 정보 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record MemberInfoDTO(
        @Schema(description = "회원 이메일") String email,
        @Schema(description = "회원 닉네임") String nickname,
        @Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg") String profileImage,
        @Schema(description = "쿠폰 갯수") Integer coupon,
        @Schema(description = "회원 등급") Integer grade,
        @Schema(description = "회원 포인트") Integer point) {
}