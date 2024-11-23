package com.codenear.butterfly.support.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;
import static com.codenear.butterfly.global.exception.ErrorMessage.INVALID_ID;

import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.support.domain.Inquiry;
import com.codenear.butterfly.support.domain.InquiryStatus;
import com.codenear.butterfly.support.domain.dto.InquiryListDTO;
import com.codenear.butterfly.support.domain.dto.InquiryRegisterDTO;
import com.codenear.butterfly.support.domain.repositroy.InquiryRepository;
import com.codenear.butterfly.support.exception.SupportException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private static final String RESPONSE_CONTENT = "아직 답변이 오지 않았습니다. 잠시만 기다려 주세요.";

    private final MemberService memberService;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void registerInquiry(InquiryRegisterDTO dto, MemberDTO memberDTO) {
        Member member = memberService.loadMemberByMemberId(memberDTO.getId());

        Inquiry inquiry = Inquiry.builder()
                .inquiryContent(dto.getInquiryContent())
                .responseContent(RESPONSE_CONTENT)
                .status(InquiryStatus.PENDING)
                .member(member)
                .build();

        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<InquiryListDTO> getInquiryList(MemberDTO memberDTO) {
        return inquiryRepository.findByMemberIdOrderByCreatedAtDesc(memberDTO.getId()).stream()
                .map(inquiry -> new InquiryListDTO(
                        inquiry.getId(),
                        inquiry.getInquiryContent(),
                        inquiry.getResponseContent(),
                        inquiry.getStatus(),
                        inquiry.getCreatedAt().toLocalDate()))
                .toList();
    }

    public Inquiry loadInquiry(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new SupportException(SERVER_ERROR, INVALID_ID.get(id)));
    }
}
