package com.codenear.butterfly.member.domain.repository;

import com.codenear.butterfly.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<String> findMaxNumberedNickname(String baseNickname) {
        QMember member = QMember.member;

        String maxNickname = queryFactory
                .select(member.nickname)
                .from(member)
                .where(member.nickname.startsWith(baseNickname))
                .orderBy(member.nickname.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(maxNickname);
    }
}