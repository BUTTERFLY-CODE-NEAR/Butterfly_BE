package com.codenear.butterfly.member.domain;

import com.codenear.butterfly.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String password;

    @Column(nullable = false)
    private String nickname;

    //todo : 기본 이미지 설정
    private String profileImage;

    //todo : 주소 테이블로 옮길지, 1:N 연결 고려
    private String address;

    private Integer point;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Platform platform;
}