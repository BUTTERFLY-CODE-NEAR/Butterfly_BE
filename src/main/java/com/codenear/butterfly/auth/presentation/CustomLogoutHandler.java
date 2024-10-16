package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.domain.JwtRefreshRepository;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final JwtRefreshRepository jwtRefreshRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        jwtRefreshRepository.deleteByMemberId(memberDTO.getId());
    }
}
