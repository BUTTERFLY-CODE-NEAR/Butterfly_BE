package com.codenear.butterfly.search.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codenear.butterfly.product.application.KeywordService.KEYWORDS_PREFIX;

@Service
@RequiredArgsConstructor
public class SearchService {
    public static final String SEARCH_LOG_KEY_PREFIX = "search_user:";
    public static final int SEARCH_LOG_MAX_SIZE = 5;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;

    public List<String> getRelatedKeywords(String keyword) {
        Set<Object> keywords = redisTemplate.opsForSet().members(KEYWORDS_PREFIX);
        if (keywords == null) {
            return null;
        }

        return keywords.stream()
                .map(Object::toString)
                .filter(key -> key.contains(keyword))
                .collect(Collectors.toList());
    }

    public void addSearchLog(String keyword, Member loginMember) { // todo : 추후 검색 로직에 추가해 로그 남기는 메서드 전달
        Member member = getMember(loginMember);
        String key = getKey(member);

        if (redisTemplate.opsForList().indexOf(key, keyword) != null)
            redisTemplate.opsForList().remove(key, 0, keyword);

        redisTemplate.opsForList().leftPush(key, keyword);

        Long size = redisTemplate.opsForList().size(key);

        if (size != null && size > SEARCH_LOG_MAX_SIZE)
            redisTemplate.opsForList().rightPop(key);
    }

    public List<Object> getSearchList(Member loginMember) {
        Member member = getMember(loginMember);
        return redisTemplate.opsForList().range(getKey(member), 0, -1);
    }

    public void deleteAllSearchLog(Member loginMember) {
        Member member = getMember(loginMember);
        redisTemplate.delete(getKey(member));
    }

    public void deleteSearchLog(String keyword, Member loginMember) {
        Member member = getMember(loginMember);
        String key = getKey(member);

        redisTemplate.opsForList().remove(key, 0, keyword);
    }

    private Member getMember(Member loginMember) {
        return memberRepository.findByEmailAndPlatform(loginMember.getEmail(), loginMember.getPlatform())
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }

    private static String getKey(Member member) {
        return SEARCH_LOG_KEY_PREFIX + member.getId();
    }
}
