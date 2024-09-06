package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.application.AuthService;
import com.codenear.butterfly.auth.application.MessageService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.application.jwt.JwtService;
import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.jwt.JwtUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private final MessageService messageService;
    private final MemberRepository memberRepository;

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO authRequestDTO) {
        try {
            Member member = emailRegisterService.emailRegister(authRequestDTO);
            log.info(messageService.getMessage("success.register", authRequestDTO.getEmail()));
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다. 사용자 ID: " + member.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageService.getMessage("error.register"));
        }
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        Member member = authService.registerOrLogin(authRequestDTO);

        String access = jwtUtil.createAccessJwt(member.getId());
        String refresh = jwtUtil.createRefreshJwt(member.getId());

        jwtService.addRefreshEntity(member, refresh);

        jwtService.setResponse(access, refresh, response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = jwtService.getRefreshCookie(request);

        try {
            jwtService.validateRefreshToken(refresh);
            Long memberId = jwtUtil.getMemberId(refresh);
            Optional<Member> member = memberRepository.findById(memberId);
            String accessJwt = jwtUtil.createAccessJwt(memberId);
            String refreshJwt = jwtUtil.createRefreshJwt(memberId);

            jwtService.deleteRefresh(refresh);

            jwtService.addRefreshEntity(member.get(), refreshJwt);
            jwtService.setResponse(accessJwt, refreshJwt, response);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }
}
