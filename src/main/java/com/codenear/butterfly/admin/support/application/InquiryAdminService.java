package com.codenear.butterfly.admin.support.application;

import com.codenear.butterfly.admin.support.domain.dto.InquiryDetailsResponse;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.support.application.InquiryFacade;
import com.codenear.butterfly.support.domain.Inquiry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {

    private final InquiryFacade inquiryFacade;

    public InquiryDetailsResponse getInquiryDetails(Long id) {
        Inquiry inquiry = inquiryFacade.getInquiry(id);
        Member member = inquiry.getMember();

        return InquiryDetailsResponse.fromEntity(inquiry, member);
    }
}
