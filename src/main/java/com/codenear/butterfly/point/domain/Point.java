package com.codenear.butterfly.point.domain;

import com.codenear.butterfly.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
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

    public void increasePoint(int point) {
        this.point += point;
    }
}
