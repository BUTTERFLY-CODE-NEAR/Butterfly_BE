package com.codenear.butterfly.auth.application.jwt;

import com.codenear.butterfly.auth.domain.JwtRefresh;
import com.codenear.butterfly.auth.domain.JwtRefreshRepository;
import com.codenear.butterfly.auth.jwt.CookieUtil;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import com.codenear.butterfly.member.domain.Member;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtRefreshRepository jwtRefreshRepository;

    public void addRefreshEntity(Member member, String refresh) {
        JwtRefresh refreshEntity = jwtUtil.buildRefreshEntity(member, refresh);
        jwtRefreshRepository.save(refreshEntity);
    }

    public String getRefreshCookie(HttpServletRequest request) {
        return cookieUtil.getRefreshCookie(request);
    }

    public void validateRefreshToken(String token) {
        if (token == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh 토큰이 존재하지 않습니다.");

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh 토큰 기간이 만료 되었습니다.");
        }

        String category = jwtUtil.getCategory(token);
        if (!category.equals("Refresh"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰의 유형이 Refresh 가 아닙니다.");

        Boolean isExist = jwtRefreshRepository.existsByRefresh(token);
        if (!isExist)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용할 수 없는 토큰입니다.");
    }

    public void setResponse(String accessToken, String refreshToken, HttpServletResponse response) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("Refresh", refreshToken));
    }

    public void deleteRefresh(String refresh) {
        jwtRefreshRepository.deleteByRefresh(refresh);
    }
}
