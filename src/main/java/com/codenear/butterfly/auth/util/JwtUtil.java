package com.codenear.butterfly.auth.util;

import com.codenear.butterfly.auth.domain.JwtRefresh;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    public static final String EMAIL = "email";
    public static final String PLATFORM = "platform";

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.access-token-expiration-millis}")
    private long accessTokenExpirationMillis;

    @Value("${jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256
                    .key()
                    .build()
                    .getAlgorithm());
    }

    public String createAccessJwt(Long memberId) {
        return createJwt(memberId, accessTokenExpirationMillis);
    }

    public String createRefreshJwt(Long memberId) {
        return createJwt(memberId, refreshTokenExpirationMillis);
    }

    public void isExpired(String token) {
         Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public Long getMemberId(String token) {
        return Long.parseLong(Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }

    public JwtRefresh buildRefreshEntity(Long memberId, String refresh) {
        return JwtRefresh.builder()
                .memberId(memberId)
                .refresh(refresh)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMillis))
                .build();
    }

    private String createJwt(Long memberId, Long tokenExpirationMillis) {
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMillis))
                .signWith(secretKey)
                .compact();
    }
}
