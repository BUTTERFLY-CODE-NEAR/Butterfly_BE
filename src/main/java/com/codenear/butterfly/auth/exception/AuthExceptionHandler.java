package com.codenear.butterfly.auth.exception;

import com.codenear.butterfly.auth.exception.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException() {
        String message = messageUtil.getMessage("error.emailAlreadyInUse");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException() {
        String message = messageUtil.getMessage("error.responseStatusException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}