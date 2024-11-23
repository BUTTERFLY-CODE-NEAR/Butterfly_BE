package com.codenear.butterfly.support.application;

import com.codenear.butterfly.support.domain.Inquiry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InquiryFacade {

    private final InquiryService inquiryService;

    public Inquiry getInquiry(Long id) {
        return inquiryService.loadInquiry(id);
    }
}
