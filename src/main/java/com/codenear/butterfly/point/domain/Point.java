package com.codenear.butterfly.point.domain;

import com.codenear.butterfly.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Point {

    @Id
    private Long id;

    private Integer point;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Builder(builderMethodName = "createPoint")
    public Point(Member member) {
        this.member = member;
        point = 0;
    }

    public void increasePoint(int point) {
        this.point += point;
    }
}
