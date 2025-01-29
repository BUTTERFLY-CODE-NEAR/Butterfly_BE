package com.codenear.butterfly.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(title = "CODE NEAR",
                description = "CODE NEAR 나비 API 문서",
                version = "0.0.1"))
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");
        Components component = new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .description("토큰의 Bearer 제외한 내용 입력")
        );

        return new OpenAPI()
                .components(new Components())
                .addSecurityItem(securityRequirement)
                .components(component);
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        return List.of(
            createGroupedOpenApi("회원가입, 로그인 API", "/auth/**", "/oauth/**", "/logout"),
            createGroupedOpenApi("유저 API", "/member/**","/certify/**","/fcm/**"),
            createGroupedOpenApi("푸시, 알림 API (notify)", "/notify/**"),
            createGroupedOpenApi("상품 API", "/products/**"),
            createGroupedOpenApi("고객 문의 API", "/support/**"),
            createGroupedOpenApi("검색 API", "/search/**"),
            createGroupedOpenApi("주소 API", "/address/**"),
            createGroupedOpenApi("카카오페이 단건결제 API", "/payment/**"),
            createGroupedOpenApi("수신 동의 API", "/consent/**"),
            createGroupedOpenApi("아이디/비밀번호 찾기 API", "/member/credential/**")
        );
    }

    private GroupedOpenApi createGroupedOpenApi(String groupName, String... paths) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(paths)
                .build();
    }
}
