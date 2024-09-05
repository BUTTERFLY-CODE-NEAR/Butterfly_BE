package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.domain.dto.AuthRequestDTO;
import com.codenear.butterfly.auth.application.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequestDTO authRequestDTO) {
        authService.registerOrLogin(authRequestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
