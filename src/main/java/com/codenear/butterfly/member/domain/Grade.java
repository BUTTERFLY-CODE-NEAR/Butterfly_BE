package com.codenear.butterfly.member.domain;

import lombok.Getter;

@Getter
public enum Grade {
    EGG(1),
    LARBA(2),
    PUPA(3),
    BUTTERFLY(4),
    ADMIN(400);

    private final int grade;

    Grade(int grade) {
        this.grade = grade;
    }
}
