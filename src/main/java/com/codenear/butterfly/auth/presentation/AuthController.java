package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.application.MessageService;
import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final JwtService jwtService;
    private final EmailRegisterService emailRegisterService;
    private final EmailLoginService emailLoginService;
    private final MessageService messageService;
    private final MemberRepository memberRepository;

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO authRequestDTO) {
        log.info(messageService.getMessage("log.registerRequest", authRequestDTO.getEmail()));

        try {
            Member member = emailRegisterService.emailRegister(authRequestDTO);
            log.info(messageService.getMessage("log.registerSuccess", member.getId()));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(messageService.getMessage("success.register", member.getEmail()));
        } catch (RuntimeException e) {
            log.error(messageService.getMessage("log.registerFailure", e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(messageService.getMessage("error.register"));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        log.info(messageService.getMessage("log.loginRequest", authRequestDTO.getEmail(), authRequestDTO.getPlatform()));

        String accessToken;
        String refreshToken;
        Long memberId;

        try {
            if (authRequestDTO.getPlatform() == Platform.CODENEAR) {
                log.info(messageService.getMessage("log.loginEmailAttempt", authRequestDTO.getEmail()));
                CustomUserDetails userDetails = emailLoginService.login(authRequestDTO.getEmail(), authRequestDTO.getPassword());
                memberId = userDetails.getId();
                log.info(messageService.getMessage("log.loginEmailSuccess", memberId));
            } else {
                log.info(messageService.getMessage("log.loginSocialAttempt", authRequestDTO.getEmail()));
                Member member = authService.registerOrLogin(authRequestDTO);
                memberId = member.getId();
                log.info(messageService.getMessage("log.loginSocialSuccess", memberId));
            }

            accessToken = jwtUtil.createAccessJwt(memberId);
            refreshToken = jwtUtil.createRefreshJwt(memberId);
            log.info(messageService.getMessage("log.jwtCreated", memberId));

            jwtService.addRefreshEntity(memberRepository.findById(memberId).orElseThrow(), refreshToken);
            jwtService.setResponse(accessToken, refreshToken, response);

            return ResponseEntity.ok(messageService.getMessage("log.loginEmailSuccess", memberId));

        } catch (BadCredentialsException e) {
            log.error(messageService.getMessage("error.badCredentials", authRequestDTO.getEmail()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(messageService.getMessage("error.badCredentials", authRequestDTO.getEmail()));
        } catch (Exception e) {
            log.error(messageService.getMessage("error.internalServerError", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(messageService.getMessage("error.internalServerError", e.getMessage()));
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = jwtService.getRefreshCookie(request);
        log.info(messageService.getMessage("log.tokenRefreshRequest", refresh));

        try {
            jwtService.validateRefreshToken(refresh);
            Long memberId = jwtUtil.getMemberId(refresh);
            Optional<Member> member = memberRepository.findById(memberId);
            String accessJwt = jwtUtil.createAccessJwt(memberId);
            String refreshJwt = jwtUtil.createRefreshJwt(memberId);

            jwtService.deleteRefresh(refresh);
            jwtService.addRefreshEntity(member.get(), refreshJwt);
            jwtService.setResponse(accessJwt, refreshJwt, response);

            log.info(messageService.getMessage("log.tokenRefreshSuccess", memberId));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResponseStatusException e) {
            log.error(messageService.getMessage("log.tokenRefreshFailure", e.getMessage()));
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }
}