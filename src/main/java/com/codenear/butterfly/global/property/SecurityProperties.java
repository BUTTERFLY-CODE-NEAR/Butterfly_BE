package com.codenear.butterfly.global.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityProperties {
    @Value("${SECURITY_WHITELIST:}")
    private String whitelist;

    public String[] getWhitelistArray() {
        return whitelist.split(",");
    }
}