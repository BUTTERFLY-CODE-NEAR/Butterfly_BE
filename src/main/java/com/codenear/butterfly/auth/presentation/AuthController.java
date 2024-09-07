package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.application.MessageService;
import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.domain.dto.CustomUserDetails;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthControllerSwagger{
    private final AuthService authService;
    private final JwtService jwtService;
    private final EmailRegisterService emailRegisterService;
    private final MessageService messageService;
    private final EmailLoginService emailLoginService;

    @PostMapping("/register")
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

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO requestDTO, HttpServletResponse response) {
        log.info(messageService.getMessage("log.loginRequest", requestDTO.getEmail(), requestDTO.getPlatform()));

        try {
            if (requestDTO.getPlatform() == Platform.CODENEAR) {
                log.info(messageService.getMessage("log.loginEmailAttempt", requestDTO.getEmail()));

                CustomUserDetails loginUser = emailLoginService.login(requestDTO.getEmail(), requestDTO.getPassword());
                jwtService.processTokens(requestDTO.getEmail(), requestDTO.getPlatform().name(), response);
                log.info(messageService.getMessage("log.jwtCreated", loginUser.getEmail()));
                log.info(messageService.getMessage("log.loginEmailSuccess", loginUser.getEmail()));

                return ResponseEntity.ok(messageService.getMessage("log.loginEmailSuccess", loginUser.getEmail()));
            } else {
                log.info(messageService.getMessage("log.loginSocialAttempt", requestDTO.getEmail()));

                authService.registerOrLogin(requestDTO);
                jwtService.processTokens(requestDTO.getEmail(), requestDTO.getPlatform().name(), response);
                log.info(messageService.getMessage("log.jwtCreated", requestDTO.getEmail()));
                log.info(messageService.getMessage("log.loginSocialSuccess", requestDTO.getEmail()));

                return ResponseEntity.ok(messageService.getMessage("log.loginSocialSuccess", requestDTO.getEmail()));
            }
        } catch (BadCredentialsException e) {
            log.error(messageService.getMessage("error.badCredentials", requestDTO.getEmail()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(messageService.getMessage("error.badCredentials", requestDTO.getEmail()));
        } catch (Exception e) {
            log.error(messageService.getMessage("error.internalServerError", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(messageService.getMessage("error.internalServerError", e.getMessage()));
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtService.processReissue(request, response);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }
}