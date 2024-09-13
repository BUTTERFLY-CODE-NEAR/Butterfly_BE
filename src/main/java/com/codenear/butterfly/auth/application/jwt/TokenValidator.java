package com.codenear.butterfly.auth.application.jwt;

import com.codenear.butterfly.auth.domain.JwtRefreshRepository;
import com.codenear.butterfly.auth.exception.AuthException;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.codenear.butterfly.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class TokenValidator {
    private final JwtUtil jwtUtil;
    private final JwtRefreshRepository jwtRefreshRepository;

    public void validateRefreshToken(String token) {
        if (token == null)
            throw new AuthException(NULL_JWT_REFRESH_TOKEN, null);

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_JWT_REFRESH_TOKEN, null);
        } catch (SignatureException e) {
            throw new AuthException(INVALID_JWT_REFRESH_SIGNATURE, null);
        }

        Boolean isExist = jwtRefreshRepository.existsByRefresh(token);
        if (!isExist)
            throw new AuthException(BLACKLIST_JWT_REFRESH_TOKEN, null);
    }
}
