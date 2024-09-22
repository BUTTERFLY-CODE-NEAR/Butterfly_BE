package com.codenear.butterfly.support.domain.dto;

import com.codenear.butterfly.support.domain.InquiryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "고객 문의 리스트 JSON", description = "고객 문의 리스트 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record InquiryListDTO(
        @Schema(description = "문의 순번") Long id,
        @Schema(description = "문의 내용") String inquiryContent,
        @Schema(description = "문의 응답") String responseContent,
        @Schema(description = "문의 상태") InquiryStatus status) {
}
