package com.codenear.butterfly.member.application;

import com.codenear.butterfly.member.domain.repository.MemberRepository;
import com.codenear.butterfly.member.util.NicknameList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final MemberRepository memberRepository;

    public Map<String, String> nicknameGenerator() {
        String baseNickname = generateBaseNickname();

        if (!isNicknameExists(baseNickname)) {
            return createResponse(baseNickname);
        }

        int maxNumber = findMaxNumberedNickname(baseNickname)
                .map(this::extractNumberFromNickname)
                .orElse(0);

        String generatedNickname = maxNumber == 0 ? baseNickname + "1" : baseNickname + (maxNumber + 1);
        return createResponse(generatedNickname);
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

    private boolean isNicknameExists(String baseNickname) {
        return memberRepository.findMaxNumberedNickname(baseNickname).isPresent();
    }

    private String generateBaseNickname() {
        return NicknameList.getRandomNickname();
    }

    private Map<String, String> createResponse(String nickname) {
        Map<String, String> response = new HashMap<>();
        response.put("nickname", nickname);
        return response;
    }
}