package com.codenear.butterfly.notify.alarm.domain;

import com.codenear.butterfly.global.domain.BaseEntity;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyConverter;
import com.codenear.butterfly.notify.NotifyMessage;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = NotifyConverter.class)
    private NotifyMessage notifyMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean isNew;

    @Builder
    public Alarm(final NotifyMessage message, final Member member) {
        this.notifyMessage = message;
        this.member = member;
        this.isNew = true;
    }
}
