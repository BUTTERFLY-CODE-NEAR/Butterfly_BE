package com.codenear.butterfly.global.config;

import com.codenear.butterfly.auth.jwt.JwtFilter;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import com.codenear.butterfly.global.property.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;


@Configuration
@EnableWebSecurity
public class OSecurityConfig {

    private final SecurityProperties securityProperties;
    private final JwtUtil jwtUtil;

    public OSecurityConfig(SecurityProperties securityProperties, JwtUtil jwtUtil) {
        this.securityProperties = securityProperties;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors((corsCustomizer) -> corsCustomizer.configurationSource(request -> {

            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(Collections.singletonList("*")); // todo : 추후 FE 서버 아이피 변경
            configuration.setAllowedMethods(Collections.singletonList("*")); // todo : 요청 메서드 전체 허용을 추후에 변경 보안상 변경
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);

            return configuration;
        }));

        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(securityProperties.getWhitelistArray()).permitAll()
                        .requestMatchers("/test").hasAuthority("ROLE_USER")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(securityProperties.getWhitelistArray())
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


        return http.build();
    }
}