package com.codenear.butterfly.global.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Component
public class SecurityProperties {
    @Value("${SECURITY_WHITELIST:}")
    private String whitelist;

    public String[] getWhitelistArray() {
        if (whitelist == null) {
            return new String[]{};
        }

        return Arrays.stream(whitelist.split(","))
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
    }
}