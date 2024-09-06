package com.codenear.butterfly.auth.jwt;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(authorization);
        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(authorization);

        if (!category.equals("Access")) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        Long memberId = jwtUtil.getMemberId(authorization);
        Optional<Member> member = memberRepository.findById(memberId);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                member.get(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
