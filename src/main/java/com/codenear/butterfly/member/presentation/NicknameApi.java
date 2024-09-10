package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.member.application.NicknameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nickname")
public class NicknameApi {

    private final NicknameService nicknameService;

    @GetMapping("/generate")
    public String nicknameGenerate() {
        return nicknameService.nicknameGenerator();
    }
}