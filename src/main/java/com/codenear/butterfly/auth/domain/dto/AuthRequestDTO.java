package com.codenear.butterfly.auth.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String email;
    private String nickname;
    private Platform platform;
}
