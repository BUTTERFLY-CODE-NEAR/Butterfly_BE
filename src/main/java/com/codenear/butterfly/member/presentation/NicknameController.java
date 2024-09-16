package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.application.NicknameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class NicknameController implements NicknameApiSwagger {

    private final NicknameService nicknameService;

    @GetMapping("/nickname/generate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseDTO> nicknameGenerate() {
        return ResponseUtil.createSuccessResponse(HttpStatus.OK, nicknameService.generateNickname(), null);
    }
}