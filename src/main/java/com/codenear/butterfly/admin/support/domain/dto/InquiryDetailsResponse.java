package com.codenear.butterfly.admin.support.domain.dto;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.support.domain.Inquiry;
import com.codenear.butterfly.support.domain.InquiryStatus;
import java.time.LocalDateTime;

public record InquiryDetailsResponse(
        Long id,
        InquiryMember member,
        InquiryDetails details
) {

    private record InquiryMember(
            String name,
            String nickname,
            String email,
            String phoneNumber
    ) {
    }

    private record InquiryDetails(
            String content,
            String answer,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            InquiryStatus status
    ) {
    }

    public static InquiryDetailsResponse fromEntity(Inquiry inquiry, Member member) {
        return new InquiryDetailsResponse(
                inquiry.getId(),
                createInquiryMember(member),
                crateInquiryDetails(inquiry)
        );
    }

    private static InquiryDetails createInquiryDetails(Inquiry inquiry) {
        return new InquiryDetails(
                inquiry.getInquiryContent(),
                inquiry.getResponseContent(),
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt(),
                inquiry.getStatus()
        );
    }

    private static InquiryMember createInquiryMember(Member member) {
        return new InquiryMember(
                member.getUsername(),
                member.getNickname(),
                member.getEmail(),
                member.getPhoneNumber()
        );
    }
}
