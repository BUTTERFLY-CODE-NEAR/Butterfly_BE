package com.codenear.butterfly.consent.domain;

import com.codenear.butterfly.global.domain.BaseEntity;
import com.codenear.butterfly.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Consent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConsentType consentType;

    private boolean isAgreed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void toggleAgreement() {
        this.isAgreed = !this.isAgreed;
    }
}
