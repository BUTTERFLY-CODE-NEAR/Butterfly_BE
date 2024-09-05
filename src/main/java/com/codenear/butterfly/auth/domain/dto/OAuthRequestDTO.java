package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import lombok.Getter;

@Getter
public class OAuthRequestDTO {
    private String email;
    private String password;
    private String nickname;
    private Platform platform;
}
