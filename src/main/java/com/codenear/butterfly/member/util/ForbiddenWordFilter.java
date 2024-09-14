package com.codenear.butterfly.member.util;

import com.codenear.butterfly.member.domain.ForbiddenWord;
import com.codenear.butterfly.member.domain.repository.nickname.ForbiddenWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ForbiddenWordFilter {

    private final ForbiddenWordRepository forbiddenWordRepository;
    private TrieNode root;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        root = new TrieNode();
        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        System.out.println("Forbidden words from DB: " + forbiddenWords);
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
        String filteredText = text.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣]", "");

        for (int i = 0; i < filteredText.length(); i++) {
            TrieNode node = root;
            for (int j = i; j < filteredText.length(); j++) {
                node = node.getChildren().get(filteredText.charAt(j));
                if (node == null) {
                    break;
                }
                if (node.isEndOfWord()) {
                    return true;
                }
            }
        }
        return false;
    }
}