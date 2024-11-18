package com.codenear.butterfly.admin.support.application;

import static com.codenear.butterfly.global.exception.ErrorCode.*;

import com.codenear.butterfly.admin.exception.AdminException;
import com.codenear.butterfly.admin.support.domain.dto.FAQAdminRequest;
import com.codenear.butterfly.support.domain.FAQ;
import com.codenear.butterfly.support.domain.repositroy.FAQRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FAQAdminService {

    private final FAQRepository faqRepository;

    public List<FAQ> getFAQList() {
        return faqRepository.findAll();
    }

    public FAQ getFAQ(Long id) {
        return loadFAQ(id);
    }

    public FAQ createFAQ(FAQAdminRequest request) {
        FAQ faq = request.toEntity();
        return faqRepository.save(faq);
    }

    public void updateFAQ(Long id, FAQAdminRequest request) {
        FAQ faq = loadFAQ(id);
        faq.updateFAQ(request);
    }

    public void deleteFAQ(Long id) {
        faqRepository.deleteById(id);
    }

    private FAQ loadFAQ(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new AdminException(SERVER_ERROR, null));
    }
}
