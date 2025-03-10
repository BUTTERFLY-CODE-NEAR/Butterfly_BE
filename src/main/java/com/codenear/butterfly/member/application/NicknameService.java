package com.codenear.butterfly.member.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.NicknameDTO;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.member.util.NicknameList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NicknameService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public NicknameDTO nicknameResponse() {
        String generatedNickname = generateNickname();
        return new NicknameDTO(generatedNickname);
    }

    public String generateNickname() {
        try {
            String baseNickname = generateBaseNickname();

            if (!isNicknameExists(baseNickname)) {
                return baseNickname;
            }

            int maxNumber = findMaxNumberedNickname(baseNickname)
                    .map(this::extractNumberFromNickname)
                    .orElse(0);

            return maxNumber == 0 ? baseNickname + "1" : baseNickname + (maxNumber + 1);
        } catch (MemberException e) {
            throw new MemberException(ErrorCode.NICKNAME_GENERATION_FAILED, null);
        }
    }

    @CacheEvict(value = "userCache", key = "#memberId")
    public void updateNickname(Long memberId, String newNickname) {
        validatorNicknameDuplication(newNickname);

        Member member = memberService.loadMemberByMemberId(memberId);
        member.setNickname(newNickname);
    }

    private Optional<String> findMaxNumberedNickname(String baseNickname) {
        try {
            return memberRepository.findMaxNumberedNickname(baseNickname);
        } catch (DataAccessException e) {
            throw new MemberException(ErrorCode.SERVER_ERROR, baseNickname);
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

    private void validatorNicknameDuplication(String nickname) {
        if (isNicknameDuplication(nickname))
            throw new MemberException(ErrorCode.NICKNAME_ALREADY_IN_USE, null);
    }

    private boolean isNicknameDuplication(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    private boolean isNicknameExists(String baseNickname) {
        return memberRepository.findMaxNumberedNickname(baseNickname).isPresent();
    }

    private String generateBaseNickname() {
        return NicknameList.getRandomNickname();
    }
}