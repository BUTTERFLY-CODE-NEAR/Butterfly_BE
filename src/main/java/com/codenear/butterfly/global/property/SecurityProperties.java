package com.codenear.butterfly.global.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Component
public class SecurityProperties {
    @Value("${SECURITY_WHITELIST}")
    private String whitelist;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

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
}