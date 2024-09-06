package com.codenear.butterfly.auth.jwt;

import com.codenear.butterfly.auth.domain.JwtRefresh;
import com.codenear.butterfly.member.domain.Member;
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

    public String createAccessJwt(Long memberId) {
        return createJwt("Access", memberId, accessTokenExpirationMillis);
    }

    public String createRefreshJwt(Long memberId) {
        return createJwt("Refresh", memberId, refreshTokenExpirationMillis);
    }

    private String createJwt(String category, Long memberId, Long tokenExpirationMillis) {
        return Jwts.builder()
                .claim("category", category)
                .claim("memberId", String.valueOf(memberId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMillis))
                .signWith(secretKey)
                .compact();
    }

    public String getCategory(String token) {
        return getMemberInfo(token, "category");
    }

    public Long getMemberId(String token) {
        return Long.parseLong(getMemberInfo(token, "memberId"));
    }

    private String getMemberInfo(String token, String info) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(info, String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public JwtRefresh buildRefreshEntity(Member member, String refresh) {
        return JwtRefresh.builder()
                .member(member)
                .refresh(refresh)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMillis))
                .build();
    }
}
