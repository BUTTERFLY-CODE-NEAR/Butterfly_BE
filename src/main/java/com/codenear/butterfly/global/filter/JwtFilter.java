package com.codenear.butterfly.global.filter;

import com.codenear.butterfly.auth.util.JwtUtil;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.property.SecurityProperties;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_UTF_8 = "UTF-8";

    private final JwtUtil jwtUtil;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURL = request.getRequestURI();

        if (securityProperties.isWhitelisted(requestURL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (isTokenNull(response, authorization)) return;

        String token = authorization.split(" ")[1];

        if (isTokenExpired(response, token)) return;

        String email = jwtUtil.getEmail(token);
        String platform = jwtUtil.getPlatform(token);

        Member member = Member.builder()
                .email(email)
                .platform(Platform.valueOf(platform))
                .build();

        setAuthentication(member);
        filterChain.doFilter(request, response);
    }

    private boolean isTokenNull(HttpServletResponse response, String accessToken) throws IOException {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            writeResponse(response, ErrorCode.NULL_JWT_ACCESS_TOKEN);
            return true;
        }
        return false;
    }

    private boolean isTokenExpired(HttpServletResponse response, String accessToken) throws IOException {
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            writeResponse(response, ErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
            return true;
        } catch (SignatureException e) {
            writeResponse(response, ErrorCode.INVALID_JWT_ACCESS_SIGNATURE);
            return true;
        }
        return false;
    }

    // 인증 정보 저장
    private static void setAuthentication(Member member) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                member,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    // Filter 에러 응답 생성
    private void writeResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARSET_UTF_8);

        ResponseDTO responseDTO = new ResponseDTO(
                errorCode.getCode(),
                errorCode.getMessage(),
                null);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);

        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}
