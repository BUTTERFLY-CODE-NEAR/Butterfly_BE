package com.codenear.butterfly.auth.jwt;

import com.codenear.butterfly.auth.domain.dto.OAuthRequestDTO;
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

//    @Value("${jwt.refresh-token-expiration-millis}")
//    private long refreshTokenExpirationMillis;

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

    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("mail", String.class);
    }

    public String getPlatform(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("platform", String.class);
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

    public String createJwt(OAuthRequestDTO authRequestDTO) {
        return Jwts.builder()
                .claim("mail", authRequestDTO.getEmail())
                .claim("nickname", authRequestDTO.getNickname())
                .claim("platform", authRequestDTO.getPlatform().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMillis))
                .signWith(secretKey)
                .compact();
    }
}
