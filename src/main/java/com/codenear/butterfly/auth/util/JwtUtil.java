package com.codenear.butterfly.auth.util;

import com.codenear.butterfly.auth.domain.JwtRefresh;
import com.codenear.butterfly.member.domain.Platform;
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

    @PostConstruct // 스프링 빈 초기화 이후 실행되는 로직
    public void init() {
        this.secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256
                    .key()
                    .build()
                    .getAlgorithm());
    }

    public String createAccessJwt(String email, String platform) {
        return createJwt(email, platform, accessTokenExpirationMillis);
    }

    public String createRefreshJwt(String email, String platform) {
        return createJwt(email, platform, refreshTokenExpirationMillis);
    }

    public String getEmail(String token) {
        return getTokenInfo(token, EMAIL);
    }

    public String getPlatform(String token) {
        return getTokenInfo(token, PLATFORM);
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

    public JwtRefresh buildRefreshEntity(String email, Platform platform, String refresh) {
        return JwtRefresh.builder()
                .email(email)
                .platform(platform)
                .refresh(refresh)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMillis))
                .build();
    }

    private String createJwt(String email, String platform, Long tokenExpirationMillis) {
        return Jwts.builder()
                .claim(EMAIL, email)
                .claim(PLATFORM, platform)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMillis))
                .signWith(secretKey)
                .compact();
    }

    private String getTokenInfo(String token, String info) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(info, String.class);
    }
}
