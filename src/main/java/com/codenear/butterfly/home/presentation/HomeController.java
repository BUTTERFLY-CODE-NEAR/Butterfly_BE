package com.codenear.butterfly.home.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 서비스 소개 페이지 컨트롤러
 */
@Controller
public class HomeController {
    @GetMapping
    public String home() {
        return "home";
    }
}
