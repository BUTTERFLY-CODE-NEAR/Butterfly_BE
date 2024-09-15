package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.member.application.NicknameService;
import com.codenear.butterfly.member.domain.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class NicknameApi implements NicknameApiSwagger {

    private final NicknameService nicknameService;

    @GetMapping("/nickname/generate")
    @ResponseStatus(HttpStatus.OK)
    public Nickname nicknameGenerate() {
        return nicknameService.nicknameResponse();
    }
}