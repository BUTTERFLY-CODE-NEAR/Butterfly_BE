package com.codenear.butterfly.consent.application;

import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.consent.dto.ConsentInfoResponseDTO;
import com.codenear.butterfly.consent.dto.ConsentSingleResponseDTO;
import com.codenear.butterfly.consent.infrastructure.ConsentDataAccess;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsentService {

    private final ConsentDataAccess consentDataAccess;

    public ConsentInfoResponseDTO getConsentsInfo(MemberDTO memberDTO) {
        List<Consent> consents = consentDataAccess.findConsents(memberDTO.getId());
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

    private ConsentSingleResponseDTO createConsentSingleResponseDTO(ConsentType value, boolean agreed) {
        return new ConsentSingleResponseDTO(value, agreed);
    }
}
