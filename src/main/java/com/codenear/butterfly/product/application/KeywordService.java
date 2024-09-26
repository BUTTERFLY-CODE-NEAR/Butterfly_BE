package com.codenear.butterfly.product.application;

import com.codenear.butterfly.product.domain.Keyword;
import com.codenear.butterfly.product.domain.repository.KeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    public static final String KEYWORDS_PREFIX = "keywords";

    private final KeywordRepository keywordRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void loadKeywordsIntoRedis() {
        List<Keyword> keywords = keywordRepository.findAll();

        redisTemplate.delete(KEYWORDS_PREFIX);

        for (Keyword keyword : keywords) {
            redisTemplate.opsForSet().add(KEYWORDS_PREFIX, keyword.getKeyword());
        }
    }
}
