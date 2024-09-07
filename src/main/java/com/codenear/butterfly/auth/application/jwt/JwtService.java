package com.codenear.butterfly.auth.application.jwt;

import com.codenear.butterfly.auth.domain.JwtRefresh;
import com.codenear.butterfly.auth.domain.JwtRefreshRepository;
import com.codenear.butterfly.auth.jwt.CookieUtil;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import com.codenear.butterfly.member.domain.Platform;
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
    public void processTokens(String email, String platform, HttpServletResponse response) {
        String accessToken = jwtUtil.createAccessJwt(email, platform);
        String refreshToken = jwtUtil.createRefreshJwt(email, platform);

        Optional<JwtRefresh> optJwtRefresh = jwtRefreshRepository.findByEmailAndPlatform(email, Platform.valueOf(platform));
        optJwtRefresh.ifPresent(jwtRefresh -> jwtRefreshRepository.deleteByRefresh(jwtRefresh.getRefresh()));

        addRefreshEntity(email, platform, refreshToken); // Refresh 토큰은 DB 저장

        setResponse(accessToken, refreshToken, response);
    }

    // Access 토큰 재발급
    public void processReissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshCookie(request);

        tokenValidator.validateRefreshToken(refreshToken); // Refresh 토큰의 유효성 검증

        jwtRefreshRepository.deleteByRefresh(refreshToken); // 기존 Refresh 토큰 데이터 삭제

        String email = jwtUtil.getEmail(refreshToken);
        String platform = jwtUtil.getPlatform(refreshToken);
        processTokens(email, platform, response);
    }

    public void addRefreshEntity(String email, String platform, String refresh) {
        JwtRefresh refreshEntity = jwtUtil.buildRefreshEntity(email, Platform.valueOf(platform), refresh);
        jwtRefreshRepository.save(refreshEntity);
    }

    public void setResponse(String accessToken, String refreshToken, HttpServletResponse response) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("Refresh", refreshToken));
    }
}
