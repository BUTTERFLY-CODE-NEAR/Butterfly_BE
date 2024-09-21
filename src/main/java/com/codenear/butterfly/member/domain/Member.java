package com.codenear.butterfly.member.domain;

import com.codenear.butterfly.global.domain.BaseEntity;
import com.codenear.butterfly.point.domain.Point;
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

    private String profileImage;

    //todo : 주소 테이블로 옮길지, 1:N 연결 고려
    private String address;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Point point;

    public void setPoint(Point point) {
        this.point = point;
        point.setMember(this);
    }
}