package com.codenear.butterfly.global.exception;

import com.codenear.butterfly.global.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.codenear.butterfly.global.util.ResponseUtil.createErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String ERROR_MESSAGE = "에러 발생 : {}";
    public static final String FIELD = "field";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> validationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        Map<String, String> filed = new HashMap<>();
        filed.put(FIELD, fieldError.getField());

        log.warn(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.VALIDATION_FAILED, fieldError.getDefaultMessage(), filed);
    }

    @ExceptionHandler(BusinessBaseException.class)
    public ResponseEntity<ResponseDTO> businessException(BusinessBaseException ex) {
        log.warn(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ex.getErrorCode(), ex.getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> checkException(Exception ex) {
        log.error(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.SERVER_ERROR, null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO> uncheckException(Exception ex) {
        log.error(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.SERVER_ERROR, null);
    }
}