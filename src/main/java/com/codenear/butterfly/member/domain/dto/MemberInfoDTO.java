package com.codenear.butterfly.member.domain.dto;

import com.codenear.butterfly.member.domain.Grade;

public record MemberInfoDTO(String nickname, String profileImage, Grade grade, Integer point) {
}
