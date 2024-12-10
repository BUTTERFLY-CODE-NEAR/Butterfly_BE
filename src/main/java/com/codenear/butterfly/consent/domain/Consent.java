package com.codenear.butterfly.consent.domain;

import com.codenear.butterfly.global.domain.BaseEntity;
import com.codenear.butterfly.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JsonIgnore
    private Member member;

    public static Consent create(ConsentType consentType, boolean agreed, Member member) {
        return Consent.builder()
                .consentType(consentType)
                .isAgreed(agreed)
                .member(member)
                .build();
    }

    public static Consent create(ConsentType consentType, Member member) {
        return create(consentType, false, member);
    }

    public void toggleAgreement() {
        this.isAgreed = !this.isAgreed;
    }

    public boolean isTopicConsented() {
        return isAgreed && consentType.hasTopic();
    }

    public boolean isSameConsentType(ConsentType consentType) {
        return this.getConsentType() == consentType;
    }
}
