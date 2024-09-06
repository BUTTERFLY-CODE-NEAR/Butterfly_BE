package com.codenear.butterfly.auth.application.jwt;

import com.codenear.butterfly.auth.domain.JwtRefreshRepository;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class TokenValidator {
    private final JwtUtil jwtUtil;
    private final JwtRefreshRepository jwtRefreshRepository;

    public void validateRefreshToken(String token) {
        if (token == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh 토큰이 존재하지 않습니다.");

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh 토큰 기간이 만료 되었습니다.");
        } catch (SignatureException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 Refresh 토큰 입니다.");
        }

        String category = jwtUtil.getCategory(token);
        if (!category.equals("Refresh"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰의 유형이 Refresh 가 아닙니다.");

        Boolean isExist = jwtRefreshRepository.existsByRefresh(token);
        if (!isExist)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용할 수 없는 토큰입니다.");
    }
}
