package com.codenear.butterfly.admin.support.presentation;

import com.codenear.butterfly.admin.support.application.FAQService;
import com.codenear.butterfly.admin.support.domain.dto.FAQRequest;
import com.codenear.butterfly.support.domain.FAQ;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/support/faq")
public class AdminFAQController {

    private final FAQService faqService;

    @GetMapping
    public String viewFAQList(Model model) {
        List<FAQ> faqs = faqService.getFAQList();
        model.addAttribute("faqs", faqs);
        return "admin/support/faq";
    }

    @GetMapping("/{id}")
    public String viewFAQ(@PathVariable Long id, Model model) {
        FAQ faq = faqService.getFAQ(id);
        model.addAttribute("faq", faq);
        return "admin/support/view";
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createFAQ(@RequestBody FAQRequest faqRequest) {
        faqService.createFAQ(faqRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/edit")
    @ResponseBody
    public ResponseEntity<Void> editFAQ(@PathVariable Long id, @RequestBody FAQRequest faqRequest) {
        faqService.updateFAQ(id, faqRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/delete")
    public String deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return "redirect:/admin/support/faq";
    }
}
