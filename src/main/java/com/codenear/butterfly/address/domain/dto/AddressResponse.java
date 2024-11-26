package com.codenear.butterfly.address.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주소 목록, 상세 JSON", description = "주소 목록, 상세 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record AddressResponse(
        @Schema(description = "주소 ID") Long id,
        @Schema(description = "주소 이름") String addressName,
        @Schema(description = "주소") String address,
        @Schema(description = "상세 주소") String detailedAddress,
        @Schema(description = "현관 비밀번호") String entrancePassword,
        @Schema(description = "배달비") Integer deliveryFee,
        @Schema(description = "메인 주소 여부") boolean isMainAddress) {
}
