package com.codenear.butterfly.search.application;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
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

    public void addSearchLog(String keyword, MemberDTO memberDTO) { // todo : 추후 검색 로직에 추가해 로그 남기는 메서드 전달
        String key = getKey(memberDTO);

        if (redisTemplate.opsForList().indexOf(key, keyword) != null)
            redisTemplate.opsForList().remove(key, 0, keyword);

        redisTemplate.opsForList().leftPush(key, keyword);

        Long size = redisTemplate.opsForList().size(key);

        if (size != null && size > SEARCH_LOG_MAX_SIZE)
            redisTemplate.opsForList().rightPop(key);
    }

    public List<Object> getSearchList(MemberDTO memberDTO) {
        return redisTemplate.opsForList().range(getKey(memberDTO), 0, -1);
    }

    public void deleteAllSearchLog(MemberDTO memberDTO) {
        redisTemplate.delete(getKey(memberDTO));
    }

    public void deleteSearchLog(String keyword, MemberDTO memberDTO) {
        String key = getKey(memberDTO);
        redisTemplate.opsForList().remove(key, 0, keyword);
    }

    private static String getKey(MemberDTO memberDTO) {
        return SEARCH_LOG_KEY_PREFIX + memberDTO.getId();
    }
}
