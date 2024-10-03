package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.validator.TokenValidator;
import com.codenear.butterfly.auth.domain.JwtRefresh;
import com.codenear.butterfly.auth.domain.JwtRefreshRepository;
import com.codenear.butterfly.auth.util.CookieUtil;
import com.codenear.butterfly.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtRefreshRepository jwtRefreshRepository;
    private final TokenValidator tokenValidator;

    // 새로운 Access, Refresh 토큰 발급
    public void processTokens(Long memberId, HttpServletResponse response) {
        String accessToken = jwtUtil.createAccessJwt(memberId);
        String refreshToken = jwtUtil.createRefreshJwt(memberId);

        Optional<JwtRefresh> optJwtRefresh = jwtRefreshRepository.findByMemberId(memberId);
        optJwtRefresh.ifPresent(jwtRefresh -> jwtRefreshRepository.deleteByRefresh(jwtRefresh.getRefresh()));

        addRefreshEntity(memberId, refreshToken); // Refresh 토큰은 DB 저장

        setResponse(accessToken, refreshToken, response);
    }

    // Access 토큰 재발급
    public void processReissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshCookie(request);

        tokenValidator.validateRefreshToken(refreshToken); // Refresh 토큰의 유효성 검증

        jwtRefreshRepository.deleteByRefresh(refreshToken); // 기존 Refresh 토큰 데이터 삭제

        Long memberId = jwtUtil.getMemberId(refreshToken);
        processTokens(memberId, response);
    }

    private void addRefreshEntity(Long memberId, String refresh) {
        JwtRefresh refreshEntity = jwtUtil.buildRefreshEntity(memberId, refresh);
        jwtRefreshRepository.save(refreshEntity);
    }

    private void setResponse(String accessToken, String refreshToken, HttpServletResponse response) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("Refresh", refreshToken));
    }
}
