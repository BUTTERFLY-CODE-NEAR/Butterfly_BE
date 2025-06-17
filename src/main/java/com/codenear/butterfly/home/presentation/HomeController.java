package com.codenear.butterfly.home.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 서비스 소개 페이지 컨트롤러
 */
@Controller
public class HomeController {
    @GetMapping
    public String home() {
        return "/home/home";
    }

    @GetMapping("/home/notice")
    public String notice(@RequestParam("id") int id, Model model) {
        model.addAttribute("id", id);
        return "/home/notice";
    }
}
