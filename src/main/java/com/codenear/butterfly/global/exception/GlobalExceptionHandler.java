package com.codenear.butterfly.global.exception;

import com.codenear.butterfly.global.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.codenear.butterfly.global.util.ResponseUtil.createErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String errorMessage = "에러 발생 : {}";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn(errorMessage, ex.getMessage());

        return createErrorResponse(ErrorCode.VALIDATION_FAILED, errors);
    }

    @ExceptionHandler(BusinessBaseException.class)
    public ResponseEntity<ResponseDTO> businessException(BusinessBaseException ex, HttpServletResponse response) {
        log.warn(errorMessage, ex.getMessage());
        return createErrorResponse(ex.getErrorCode(), ex.getBody());
    }
}