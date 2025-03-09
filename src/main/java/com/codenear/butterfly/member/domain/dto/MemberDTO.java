package com.codenear.butterfly.member.domain.dto;

import com.codenear.butterfly.member.domain.Grade;
import com.codenear.butterfly.member.domain.Platform;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDTO {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String nickname;
    private String profileImage;
    private Grade grade;
    private Platform platform;
    private String authorities;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public MemberDTO(Long id, String username, String email, String phoneNumber, String password, String nickname, String profileImage, Grade grade, Platform platform) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.grade = grade;
        this.platform = platform;

        if (this.grade == Grade.ADMIN) {
            this.authorities = ROLE_ADMIN;
        } else {
            this.authorities = ROLE_USER;
        }
    }
}