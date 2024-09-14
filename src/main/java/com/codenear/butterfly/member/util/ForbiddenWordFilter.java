package com.codenear.butterfly.member.util;

import com.codenear.butterfly.member.domain.ForbiddenWord;
import com.codenear.butterfly.member.domain.repository.nickname.ForbiddenWordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ForbiddenWordFilter {

    private final ForbiddenWordRepository forbiddenWordRepository;
    private TrieNode root;

    @PostConstruct
    public void init() {
        root = new TrieNode();
        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        forbiddenWords.forEach(word -> insert(word.getWord()));
    }

    private void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.getChildren().computeIfAbsent(c, k -> new TrieNode());
        }
        node.setEndOfWord(true);
    }

    public boolean containsForbiddenWord(String text) {
        TrieNode node = root;
        for (char c : text.toCharArray()) {
            node = node.getChildren().get(c);
            if (node == null) {
                return false;
            }
            if (node.isEndOfWord()) {
                return true;
            }
        }
        return false;
    }
}