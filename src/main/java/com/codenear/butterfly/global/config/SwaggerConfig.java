package com.codenear.butterfly.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "CODE NEAR",
                description = "CODE NEAR 나비 API 문서",
                version = "0.0.1"))
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi test() {
        String groupName = "회원가입 API 명세";
        String paths = "/auth/**";

        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(paths)
                .build();
    }
}
