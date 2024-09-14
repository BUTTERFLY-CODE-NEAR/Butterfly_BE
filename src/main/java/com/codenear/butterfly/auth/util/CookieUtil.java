package com.codenear.butterfly.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-token-expiration-millis}")
    private Long refreshTokenExpirationMillis;

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (refreshTokenExpirationMillis / 1000)); // setMaxAge(초) 초 단위라 Millis / 1000로 변환
        cookie.setHttpOnly(true);

        return cookie;
    }

    public String getRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;

        return Arrays.stream(cookies)
                .filter(cookie -> "Refresh".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
