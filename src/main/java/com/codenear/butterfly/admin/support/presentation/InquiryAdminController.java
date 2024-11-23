package com.codenear.butterfly.admin.support.presentation;

import com.codenear.butterfly.admin.support.application.InquiryAdminService;
import com.codenear.butterfly.admin.support.domain.dto.InquiresResponse;
import com.codenear.butterfly.admin.support.domain.dto.InquiryAnswerRequest;
import com.codenear.butterfly.admin.support.domain.dto.InquiryDetailsResponse;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/support/inquiry")
public class InquiryAdminController {

    private final InquiryAdminService inquiryAdminService;

    @GetMapping
    public String getInquiries(Model model) {
        InquiresResponse inquiries = inquiryAdminService.getInquiries();
        model.addAttribute("inquiresResponse", inquiries);
        return "admin/support/inquiry";
    }

    @GetMapping("/{id}")
    public String getInquiryDetails(Model model, @PathVariable Long id) {
        InquiryDetailsResponse inquiryDetails = inquiryAdminService.getInquiryDetails(id);
        model.addAttribute("inquiryDetails", inquiryDetails);
        return "admin/support/inquiry-details";
    }

    @PostMapping("/answer")
    public ResponseEntity<ResponseDTO> changeStatus(@RequestBody InquiryAnswerRequest request) {
        inquiryAdminService.updateAnswer(request);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/{id}/change-status")
    public ResponseEntity<ResponseDTO> changeStatus(@PathVariable Long id) {
        inquiryAdminService.updateStatus(id);
        return ResponseUtil.createSuccessResponse(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteInquiry(@PathVariable Long id) {
        inquiryAdminService.deleteInquiry(id);
        return ResponseUtil.createSuccessResponse(null);
    }
}
