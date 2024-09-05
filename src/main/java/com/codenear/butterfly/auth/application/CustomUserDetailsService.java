package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Role;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ErrorMessageService errorMessageService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        errorMessageService.getErrorMessage("error.userNotFound", email)
                ));

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getRoles().stream().map(Role::name).collect(Collectors.toSet())
        );
    }
}