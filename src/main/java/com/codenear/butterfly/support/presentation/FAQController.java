package com.codenear.butterfly.support.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.support.application.FAQService;
import com.codenear.butterfly.support.domain.dto.FAQResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support/faq")
@RequiredArgsConstructor
public class FAQController implements FAQControllerSwagger {

    private final FAQService faqService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getFAQs() {
        List<FAQResponse> faqs = faqService.getFAQs();
        return ResponseUtil.createSuccessResponse(faqs);
    }
}
