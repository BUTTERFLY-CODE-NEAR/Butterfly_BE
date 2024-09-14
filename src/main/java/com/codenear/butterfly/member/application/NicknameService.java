package com.codenear.butterfly.member.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.member.util.NicknameList;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final MemberRepository memberRepository;

    public Map<String, String> nicknameGenerator() {
        try {
            String baseNickname = generateBaseNickname();

            if (!isNicknameExists(baseNickname)) {
                return createResponse(baseNickname);
            }

            int maxNumber = findMaxNumberedNickname(baseNickname)
                    .map(this::extractNumberFromNickname)
                    .orElse(0);

            String generatedNickname = maxNumber == 0 ? baseNickname + "1" : baseNickname + (maxNumber + 1);
            return createResponse(generatedNickname);
        } catch (RuntimeException e) {
            throw new MemberException(ErrorCode.NICKNAME_GENERATION_FAILED, null);
        }
    }

    private Optional<String> findMaxNumberedNickname(String baseNickname) {
        try {
            return memberRepository.findMaxNumberedNickname(baseNickname);
        } catch (DataAccessException e) {
            throw new MemberException(ErrorCode.DATABASE_ERROR, baseNickname);
        }
    }

    private int extractNumberFromNickname(String nickname) {
        try {
            return Optional.of(nickname.replaceAll("\\D+", ""))  // 숫자만 추출
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .orElse(0);
        } catch (NumberFormatException e) {
            throw new MemberException(ErrorCode.INVALID_NICKNAME_FORMAT, nickname);
        }
    }

    private boolean isNicknameExists(String baseNickname) {
        try {
            return memberRepository.findMaxNumberedNickname(baseNickname).isPresent();
        } catch (DataAccessException e) {
            throw new MemberException(ErrorCode.DATABASE_ERROR, baseNickname);
        }
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