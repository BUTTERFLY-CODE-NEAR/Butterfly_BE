package com.codenear.butterfly.support.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "고객 문의 등록 JSON", description = "고객 문의 등록 시 정의되는 JSON 데이터 입니다. **(내용 제한 5자 ~ 1,000자)**")
@Getter
public class InquiryRegisterDTO {

    @Schema(description = "문의 내용")
    @Size(min = 5, max = 1000, message = "문의 내용은 최소 5자 이상, 최대 1,000자 이하입니다.")
    private String inquiryContent;
}