package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Keyword;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class KeywordRedisRepository {
    public static final String KEYWORDS_PREFIX = "keywords";
    private final RedisTemplate<String, Object> redisTemplate;

    public void deleteKeyword() {
        redisTemplate.delete(KEYWORDS_PREFIX);
    }

    public void saveKeyword(List<Keyword> keywords) {
        for (Keyword keyword : keywords) {
            redisTemplate.opsForSet().add(KEYWORDS_PREFIX, keyword.getKeyword());
        }
    }

    public Set<Object> getKeyword() {
        return redisTemplate.opsForSet().members(KEYWORDS_PREFIX);
    }

    public void updateKeywords(List<Keyword> newKeywords, List<Keyword> existingKeywordsInDb) {
        if (newKeywords == null || newKeywords.isEmpty()) {
            return;
        }

        Set<String> newKeywordSet = extractKeywordStrings(newKeywords);
        Set<String> existingKeywordSet = extractKeywordStrings(existingKeywordsInDb);

        Set<String> toRemove = findKeywordsToRemove(existingKeywordSet, newKeywordSet);
        Set<String> toAdd = findKeywordsToAdd(existingKeywordSet, newKeywordSet);

        removeKeywordsFromRedis(toRemove);
        addKeywordsToRedis(toAdd);
    }

    private Set<String> extractKeywordStrings(List<Keyword> keywords) {
        return keywords.stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.toSet());
    }

    private Set<String> findKeywordsToRemove(Set<String> existing, Set<String> updated) {
        return existing.stream()
                .filter(k -> !updated.contains(k))
                .collect(Collectors.toSet());
    }

    private Set<String> findKeywordsToAdd(Set<String> existing, Set<String> updated) {
        return updated.stream()
                .filter(k -> !existing.contains(k))
                .collect(Collectors.toSet());
    }

    private void removeKeywordsFromRedis(Set<String> toRemove) {
        if (!toRemove.isEmpty()) {
            redisTemplate.opsForSet().remove(KEYWORDS_PREFIX, toRemove.toArray());
        }
    }

    private void addKeywordsToRedis(Set<String> toAdd) {
        if (!toAdd.isEmpty()) {
            redisTemplate.opsForSet().add(KEYWORDS_PREFIX, toAdd.toArray());
        }
    }
}
