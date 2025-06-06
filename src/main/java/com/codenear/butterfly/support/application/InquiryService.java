package com.codenear.butterfly.support.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;
import static com.codenear.butterfly.global.exception.ErrorMessage.INVALID_ID;
import static com.codenear.butterfly.support.domain.InquiryStatus.PENDING;

import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.support.domain.Inquiry;
import com.codenear.butterfly.support.domain.dto.InquiryListDTO;
import com.codenear.butterfly.support.domain.dto.InquiryRegisterRequest;
import com.codenear.butterfly.support.domain.repositroy.InquiryRepository;
import com.codenear.butterfly.support.exception.SupportException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final MemberService memberService;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void registerInquiry(InquiryRegisterRequest request, MemberDTO memberDTO) {
        Member member = memberService.loadMemberByMemberId(memberDTO.getId());
        Inquiry inquiry = createInquiry(request, member);

        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<InquiryListDTO> getInquiryList(MemberDTO memberDTO) {
        return inquiryRepository.findByMemberIdOrderByCreatedAtDesc(memberDTO.getId()).stream()
                .map(inquiry -> new InquiryListDTO(
                        inquiry.getId(),
                        inquiry.getInquiryContent(),
                        inquiry.getAnswerByStatus(),
                        inquiry.getStatus(),
                        inquiry.getCreatedAt().toLocalDate()))
                .toList();
    }

    public Inquiry loadInquiry(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new SupportException(SERVER_ERROR, INVALID_ID.get(id)));
    }

    private static Inquiry createInquiry(InquiryRegisterRequest request, Member member) {
        return Inquiry.builder()
                .inquiryContent(request.getInquiryContent())
                .responseContent(null)
                .status(PENDING)
                .member(member)
                .build();
    }
}
