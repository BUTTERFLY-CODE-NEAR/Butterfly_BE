package com.codenear.butterfly.member.application;

import com.codenear.butterfly.member.domain.repository.MemberRepository;
import com.codenear.butterfly.member.util.KoreanCharacterRegex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final Random random = new Random();
    private final MemberRepository memberRepository;

    public String nicknameGenerator() {
        String baseNickname = generateBaseNickname();

        int maxNumber = findMaxNumberedNickname(baseNickname)
                .map(this::extractNumberFromNickname)
                .orElse(0);

        return maxNumber == 0 ? baseNickname : baseNickname + (maxNumber + 1);
    }

    private Optional<String> findMaxNumberedNickname(String baseNickname) {
        return memberRepository.findMaxNumberedNickname(baseNickname);
    }

    private int extractNumberFromNickname(String nickname) {
        return Optional.of(nickname.replaceAll("\\D+", ""))  // 숫자만 추출
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .orElse(0);
    }

    private String generateBaseNickname() {
        String nickname;
        do {
            nickname = generateRandomKoreanSyllable() + generateRandomKoreanSyllable();
        } while (!KoreanCharacterRegex.KOREAN_SYLLABLES.isValid(nickname));

        return nickname;
    }

    private String generateRandomKoreanSyllable() {
        int startUnicode = 0xAC00;
        int endUnicode = 0xD7A3;

        int randomCode = startUnicode + random.nextInt(endUnicode - startUnicode + 1);
        return String.valueOf((char) randomCode);
    }
}