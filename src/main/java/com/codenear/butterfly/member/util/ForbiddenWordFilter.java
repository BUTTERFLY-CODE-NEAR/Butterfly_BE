package com.codenear.butterfly.member.util;

import com.codenear.butterfly.member.domain.ForbiddenWord;
import com.codenear.butterfly.member.domain.repository.nickname.ForbiddenWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForbiddenWordFilter {

    private final ForbiddenWordRepository forbiddenWordRepository;

    public boolean containsForbiddenWord(String text) {
        String filteredText = text.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣]", "");

        for (ForbiddenWord word : forbiddenWordRepository.findAll()) {
            if (isForbiddenWordInText(filteredText, word.getWord())) {
                return true;
            }
        }

        return false;
    }

    private boolean isForbiddenWordInText(String text, String forbiddenWord) {
        String cleanedForbiddenWord = forbiddenWord.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣]", "");

        int index = 0;
        for (char c : text.toCharArray()) {
            if (c == cleanedForbiddenWord.charAt(index)) {
                index++;
            }
            if (index == cleanedForbiddenWord.length()) {
                return true;
            }
        }

        return false;
    }
}