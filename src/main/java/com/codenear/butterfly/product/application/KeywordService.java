package com.codenear.butterfly.product.application;

import com.codenear.butterfly.product.domain.Keyword;
import com.codenear.butterfly.product.domain.repository.KeywordRedisRepository;
import com.codenear.butterfly.product.domain.repository.KeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final KeywordRedisRepository keywordRedisRepository;

    @PostConstruct
    public void loadKeywordsIntoRedis() {
        List<Keyword> keywords = keywordRepository.findAll();

        keywordRedisRepository.deleteKeyword();
        keywordRedisRepository.saveKeyword(keywords);

    }
}
