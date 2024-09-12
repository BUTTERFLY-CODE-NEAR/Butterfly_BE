package com.codenear.butterfly.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Tag(name = "Member", description = "닉네임 생성 Api")
public interface NicknameApiSwagger {

    @Operation(summary = "닉네임 생성", description = "랜덤 닉네임 생성 API(닉네임 중복 시 숫자 태그 추가")
    @RequestMapping(value = "/nickname/generate", method = RequestMethod.GET)
    Map<String, String> nicknameGenerate();
}
