package com.codenear.butterfly.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-token-expiration-millis}")
    private Long refreshTokenExpirationMillis;

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 24 * 24);
        cookie.setHttpOnly(true);

        return cookie;
    }

    public String getRefreshCookie(HttpServletRequest request) {
        String refresh = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("Refresh"))
                refresh = cookie.getValue();

        return refresh;
    }
}
