package com.codenear.butterfly.search.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;

    public void addSearchLog(String keyword, Member loginMember) {
        Member member = getMember(loginMember);

        String key = "search_user:" + member.getId();
        redisTemplate.opsForList().leftPush(key, keyword);
        redisTemplate.opsForList().trim(key, 0, 9);
    }

    public List<Object> getSearchList(Member loginMember) {
        Member member = getMember(loginMember);

        String key = "search_user:" + member.getId();
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    private Member getMember(Member loginMember) {
        return memberRepository.findByEmailAndPlatform(loginMember.getEmail(), loginMember.getPlatform())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }
}
