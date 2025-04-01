package com.codenear.butterfly.global.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ShareController {
    @GetMapping("/{id}")
    public String shareRedirect(@PathVariable Long id) {
        return "redirect:butterfly://productDetail/" + id;
    }
}
