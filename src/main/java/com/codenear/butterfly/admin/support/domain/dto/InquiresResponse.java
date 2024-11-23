package com.codenear.butterfly.admin.support.domain.dto;

import com.codenear.butterfly.support.domain.Inquiry;
import com.codenear.butterfly.support.domain.InquiryStatus;
import java.time.LocalDateTime;
import java.util.List;

public record InquiresResponse(
        List<InquiryDTO> inquiries
) {

    private record InquiryDTO(
            Long id,
            String content,
            LocalDateTime createAt,
            InquiryStatus inquiryStatus
    ) {
    }

    public static InquiresResponse fromEntity(List<Inquiry> inquiries) {
        List<InquiryDTO> inquiryDTOS = inquiries.stream()
                .map(InquiresResponse::createInquiryDTO)
                .toList();

        return new InquiresResponse(inquiryDTOS);
    }

    private static InquiryDTO createInquiryDTO(Inquiry inquiry) {
        return new InquiryDTO(
                inquiry.getId(),
                inquiry.getInquiryContent(),
                inquiry.getCreatedAt(),
                inquiry.getStatus()
        );
    }
}
