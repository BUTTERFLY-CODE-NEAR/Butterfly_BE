package com.codenear.butterfly.auth.domain;

import com.codenear.butterfly.member.domain.Platform;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class JwtRefresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String refresh;

    @Column(nullable = false)
    private Date expiration;
}
