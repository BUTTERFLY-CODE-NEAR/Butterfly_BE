package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentRepository;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.consent.dto.ConsentInfoResponseDTO;
import com.codenear.butterfly.consent.dto.ConsentSingleResponseDTO;
import com.codenear.butterfly.consent.dto.ConsentUpdateRequestDTO;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsentService {
    private final ConsentRepository consentRepository;
    private final MemberService memberService;

    public ConsentInfoResponseDTO getConsentsInfo(MemberDTO memberDTO) {
        List<Consent> consents = loadConsentsByMemberId(memberDTO);
        List<ConsentSingleResponseDTO> consentSingleDTOS = getConsentSingleResponseDTOS(consents);
        return new ConsentInfoResponseDTO(consentSingleDTOS);
    }

    private List<ConsentSingleResponseDTO> getConsentSingleResponseDTOS(List<Consent> consents) {
        List<ConsentSingleResponseDTO> responseDTOList = new ArrayList<>();

        for (ConsentType type : ConsentType.values()) {
            boolean agreed = isAgreed(consents, type);
            responseDTOList.add(createConsentSingleResponseDTO(type, agreed));
        }
        return responseDTOList;
    }

    private boolean isAgreed(List<Consent> consents, ConsentType type) {
        return consents.stream()
                .filter(consent -> consent.getConsentType().equals(type))
                .map(Consent::isAgreed)
                .findFirst()
                .orElse(false);
    }

    private  ConsentSingleResponseDTO createConsentSingleResponseDTO(ConsentType value, boolean agreed) {
        return new ConsentSingleResponseDTO(value, agreed);
    }

    private List<Consent> loadConsentsByMemberId(MemberDTO memberDTO) {
        return consentRepository.findByMemberId(memberDTO.getId());
    }

    public void updateConsent(ConsentUpdateRequestDTO updateRequestDTO, MemberDTO memberDTO) {
        List<Consent> consents = loadConsentsByMemberId(memberDTO);
        ConsentType type = updateRequestDTO.getConsentType();

        Consent consent = consents.stream()
                .filter(findConsent -> findConsent.getConsentType().equals(type))
                .findFirst()
                .orElseGet(() -> createConsent(type, false, memberService.loadMemberByMemberId(memberDTO.getId())));

        consent.toggleAgreement();
        consentRepository.save(consent);
    }

    public void saveConsent(ConsentType type, boolean agreed, Member member) {
        Consent consent = createConsent(type, agreed, member);
        consentRepository.save(consent);
    }

    private Consent createConsent(ConsentType type, boolean agreed, Member member) {
        return Consent.builder()
                .consentType(type)
                .isAgreed(agreed)
                .member(member)
                .build();
    }
}
