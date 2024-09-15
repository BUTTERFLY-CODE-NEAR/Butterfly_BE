package com.codenear.butterfly.member.presentation;

import com.codenear.butterfly.member.domain.Nickname;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Member", description = "닉네임 생성 Api")
public interface NicknameApiSwagger {

    @Operation(summary = "닉네임 생성", description = "랜덤 닉네임 생성 API(닉네임 중복 시 숫자 태그 추가)")
    @GetMapping(value = "/nickname/generate")
    Nickname nicknameGenerate();
}
