package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.application.MessageService;
import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailLoginService {

    private final CustomUserDetailsService userDetailsService;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetails login(String email, String password) {
        log.info(messageService.getMessage("log.loginAttempt", email));

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException(messageService.getMessage("error.badCredentials"));
        }

        log.info(messageService.getMessage("log.loginSuccess", email));
        return userDetails;
    }
}