package com.codenear.butterfly.fcm.application;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FCMFacade {

    private final FCMService fcmService;

    public void saveFCM(String token, MemberDTO loginMember) {
        fcmService.saveFCM(token, loginMember);
    }
}
