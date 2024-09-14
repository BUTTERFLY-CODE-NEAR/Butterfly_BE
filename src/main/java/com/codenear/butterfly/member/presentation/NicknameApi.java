package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.member.application.NicknameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class NicknameApi implements NicknameApiSwagger {

    private final NicknameService nicknameService;

    @GetMapping("/nickname/generate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> nicknameGenerate() {
        return nicknameService.nicknameGenerateResponse();
    }
}