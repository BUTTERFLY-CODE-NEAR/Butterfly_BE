package com.codenear.butterfly.admin.support.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/support/inquiry")
public class AdminInquiryController {

    @GetMapping
    public String inquiryList(Model model) {
        return "admin/support/inquiryList";
    }
}
