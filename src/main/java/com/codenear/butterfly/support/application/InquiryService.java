package com.codenear.butterfly.support.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.support.domain.Inquiry;
import com.codenear.butterfly.support.domain.InquiryRepository;
import com.codenear.butterfly.support.domain.InquiryStatus;
import com.codenear.butterfly.support.domain.dto.InquiryListDTO;
import com.codenear.butterfly.support.domain.dto.InquiryRegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private static final String RESPONSE_CONTENT = "아직 답변이 오지 않았습니다. 잠시만 기다려 주세요.";

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void registerInquiry(InquiryRegisterDTO dto, Member loginMember) {
        Member member = getMember(loginMember);

        Inquiry inquiry = Inquiry.builder()
                .inquiryContent(dto.getInquiryContent())
                .responseContent(RESPONSE_CONTENT)
                .status(InquiryStatus.PENDING)
                .member(member)
                .build();

        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<InquiryListDTO> getInquiryList(Member loginMember) {
        Member member = getMember(loginMember);

        return inquiryRepository.findByMember(member).stream()
                .map(inquiry -> new InquiryListDTO(
                    inquiry.getId(),
                    inquiry.getInquiryContent(),
                    inquiry.getResponseContent(),
                    inquiry.getStatus()))
                .toList();
    }

    private Member getMember(Member loginMember) {
        return memberRepository.findByEmailAndPlatform(loginMember.getEmail(), loginMember.getPlatform())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }
}
