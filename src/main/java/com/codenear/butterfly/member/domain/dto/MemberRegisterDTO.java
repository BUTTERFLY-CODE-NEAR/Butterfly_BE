package com.codenear.butterfly.member.domain.dto;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.point.domain.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterDTO {
    private Long id;
    private String phoneNumber;
    private Point point;

    public static MemberRegisterDTO from(Member member) {
        return MemberRegisterDTO.builder()
                .id(member.getId())
                .phoneNumber(member.getPhoneNumber())
                .point(member.getPoint())
                .build();
    }

}
