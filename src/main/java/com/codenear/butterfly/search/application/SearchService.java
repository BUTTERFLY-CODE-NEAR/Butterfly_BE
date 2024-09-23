package com.codenear.butterfly.search.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {
    public static final String SEARCH_LOG_KEY_PREFIX = "search_user:";
    public static final int SEARCH_LOG_MAX_SIZE = 5;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;

    public void addSearchLog(String keyword, Member loginMember) {
        Member member = getMember(loginMember);
        String key = getKey(member);

        redisTemplate.opsForSet().add(key, keyword);

        // 저장 검색어가 최대일 경우 삭제
        Long size = redisTemplate.opsForSet().size(key);
        if (size != null && size > SEARCH_LOG_MAX_SIZE)
            redisTemplate.opsForSet().pop(key);
    }

    public Set<Object> getSearchList(Member loginMember) {
        Member member = getMember(loginMember);
        return redisTemplate.opsForSet().members(getKey(member));
    }

    public void deleteSearchLog(Member loginMember) {
        Member member = getMember(loginMember);
        redisTemplate.delete(getKey(member));
    }

    private Member getMember(Member loginMember) {
        return memberRepository.findByEmailAndPlatform(loginMember.getEmail(), loginMember.getPlatform())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }

    private static String getKey(Member member) {
        return SEARCH_LOG_KEY_PREFIX + member.getId();
    }
}
