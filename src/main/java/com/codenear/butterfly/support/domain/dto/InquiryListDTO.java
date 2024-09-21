package com.codenear.butterfly.support.domain.dto;

import com.codenear.butterfly.support.domain.InquiryStatus;

public record InquiryListDTO(Long id, String inquiryContent, String responseContent, InquiryStatus status) {
}
