package com.codenear.butterfly.admin.support.application;

import static com.codenear.butterfly.notify.fcm.domain.FCMMessageConstant.INQUIRY_ANSWERED;

import com.codenear.butterfly.admin.support.domain.dto.InquiresResponse;
import com.codenear.butterfly.admin.support.domain.dto.InquiryAnswerRequest;
import com.codenear.butterfly.admin.support.domain.dto.InquiryDetailsResponse;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.support.application.InquiryFacade;
import com.codenear.butterfly.support.domain.Inquiry;
import com.codenear.butterfly.support.domain.repositroy.InquiryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {

    private final InquiryFacade inquiryFacade;
    private final InquiryRepository inquiryRepository;
    private final FCMFacade fcmFacade;

    public InquiresResponse getInquiries() {
        List<Inquiry> inquiries = inquiryRepository.findAll();

        return InquiresResponse.fromEntity(inquiries);
    }

    public InquiryDetailsResponse getInquiryDetails(Long id) {
        Inquiry inquiry = inquiryFacade.getInquiry(id);
        Member member = inquiry.getMember();

        return InquiryDetailsResponse.fromEntity(inquiry, member);
    }

    @Transactional
    public void updateAnswer(InquiryAnswerRequest request) {
        Inquiry inquiry = inquiryFacade.getInquiry(request.id());
        inquiry.updateResponseContent(request.answer());
    }

    @Transactional
    public void updateStatus(Long id) {
        Inquiry inquiry = inquiryFacade.getInquiry(id);
        inquiry.toggleStatus();

        if (inquiry.isAnswerStatus()) {
            fcmFacade.sendMessage(INQUIRY_ANSWERED, inquiry.getMember().getId());
        }
    }

    @Transactional
    public void deleteInquiry(Long id) {
        inquiryRepository.deleteById(id);
    }
}
