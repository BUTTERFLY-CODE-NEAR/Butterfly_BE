package com.codenear.butterfly.global.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SecurityProperties {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Value("${SECURITY_WHITELIST}")
    private String whitelist;
    @Value("${SECURITY_ALLOW_ORIGINS}")
    private String allowedOrigins;

    public String[] getWhitelistArray() {
        if (whitelist == null) {
            return new String[]{};
        }

        return Arrays.stream(whitelist.split(","))
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
    }

    public boolean isWhitelisted(String url) {
        return Arrays.stream(getWhitelistArray())
                .anyMatch(pattern -> pathMatcher.match(pattern, url));
    }

    public List<String> getAllowedOrigins() {
        if (allowedOrigins == null) {
            return new ArrayList<String>();
        }
        return Arrays.stream(allowedOrigins.split(","))
                .filter(StringUtils::hasText)
                .toList();
    }
}