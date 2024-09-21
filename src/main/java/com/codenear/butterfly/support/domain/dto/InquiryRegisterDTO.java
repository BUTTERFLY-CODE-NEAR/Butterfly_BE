package com.codenear.butterfly.support.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class InquiryRegisterDTO {

    @Size(min = 5, max = 1000, message = "문의 내용은 최소 5자 이상, 최대 1,000자 이하입니다.")
    private String inquiryContent;
}