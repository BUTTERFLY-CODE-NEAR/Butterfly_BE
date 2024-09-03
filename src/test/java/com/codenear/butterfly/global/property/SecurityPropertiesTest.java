package com.codenear.butterfly.global.property;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class SecurityPropertiesTest {

    @BeforeEach
    void setUp() {
        // 환경 변수 설정
        System.setProperty("SECURITY_WHITELIST", "/v2/api-docs,/v3/api-docs/**,/configuration/ui,/swagger-resources/**,/configuration/security,/swagger-ui/index.html,/webjars/**,/file/**,/image/**,/swagger/**,/swagger-ui/**,/h2/**");
    }

    @Test
    void getWhitelistArray() {
        // SecurityProperties 인스턴스 생성
        SecurityProperties securityProperties = new SecurityProperties();

        // whitelist 필드에 환경 변수 값 주입
        String whitelist = System.getProperty("SECURITY_WHITELIST");
        ReflectionTestUtils.setField(securityProperties, "whitelist", whitelist);

        // 결과 확인
        String[] result = securityProperties.getWhitelistArray();

        assertArrayEquals(new String[]{
                "/v2/api-docs",
                "/v3/api-docs/**",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui/index.html",
                "/webjars/**",
                "/file/**",
                "/image/**",
                "/swagger/**",
                "/swagger-ui/**",
                "/h2/**"
        }, result);
    }
}