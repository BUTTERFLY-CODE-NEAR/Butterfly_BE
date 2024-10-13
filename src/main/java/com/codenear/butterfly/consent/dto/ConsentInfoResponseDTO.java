package com.codenear.butterfly.consent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "수신 동의 리스트 JSON", description = "수신 동의 리스트 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record ConsentInfoResponseDTO(
        @Schema(description = "수신 동의 리스트") List<ConsentSingleResponseDTO> consents
) {
}
