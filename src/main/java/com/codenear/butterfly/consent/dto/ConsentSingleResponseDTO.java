package com.codenear.butterfly.consent.dto;

import com.codenear.butterfly.consent.domain.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "수신 동의 단일 JSON", description = "수신 동의 리스트 요청 시 반환되는 응답 JSON 중 단일 데이터 입니다.")
public record ConsentSingleResponseDTO(
        @Schema(description = "동의 종류") ConsentType consentType,
        @Schema(description = "동의 여부") boolean isAgreed
) {
}
