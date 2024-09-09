package com.codenear.butterfly.auth.application.email;

import com.codenear.butterfly.auth.exception.message.MessageUtil;
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
    private final MessageUtil messageUtil;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetails login(String email, String password) {
        log.info(messageUtil.getMessage("log.loginAttempt", email));

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException(messageUtil.getMessage("error.badCredentials"));
        }

        log.info(messageUtil.getMessage("log.loginSuccess", email));
        return userDetails;
    }
}