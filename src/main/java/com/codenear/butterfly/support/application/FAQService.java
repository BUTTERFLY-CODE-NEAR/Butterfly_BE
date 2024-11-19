package com.codenear.butterfly.support.application;

import com.codenear.butterfly.support.domain.FAQ;
import com.codenear.butterfly.support.domain.dto.FAQResponse;
import com.codenear.butterfly.support.domain.repositroy.FAQRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;

    public List<FAQResponse> getFAQs() {
        List<FAQ> faqs = faqRepository.findAll();

        return faqs.stream()
                .filter(FAQ::isStatus)
                .map(FAQResponse::fromEntity)
                .toList();
    }
}
