package com.codenear.butterfly.member.util;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public enum KoreanCharacterRegex {
    KOREAN_SYLLABLES(Pattern.compile("^[가-힣]{2}$"));

    private final Pattern pattern;

    KoreanCharacterRegex(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean isValid(String nickname) {
        return this.pattern.matcher(nickname).matches();
    }
}