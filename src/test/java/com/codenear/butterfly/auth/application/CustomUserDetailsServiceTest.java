package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.email.CustomUserDetailsService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MessageService messageService = new MessageService(messageSource);
        customUserDetailsService = new CustomUserDetailsService(memberRepository, messageService);
    }

    @Test
    void UserExists() {
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .username("testuser")
                .password("password123")
                .nickname("TestNickname")
                .build();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void UserDoesNotExist() {
        String email = "nonexistent@example.com";
        Locale locale = LocaleContextHolder.getLocale();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(messageSource.getMessage("error.userNotFound", new Object[]{email}, locale))
                .thenReturn("이메일 " + email + "을(를) 찾을 수 없습니다.");

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        assertEquals("이메일 " + email + "을(를) 찾을 수 없습니다.", exception.getMessage());
    }
}