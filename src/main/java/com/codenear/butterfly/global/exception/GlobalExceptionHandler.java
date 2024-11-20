package com.codenear.butterfly.global.exception;

import com.codenear.butterfly.global.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static com.codenear.butterfly.global.util.ResponseUtil.createErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String ERROR_MESSAGE = "에러 발생 : {}";
    public static final String FIELD = "field";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        Map<String, String> filed = new HashMap<>();
        filed.put(FIELD, fieldError.getField());

        log.warn(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.VALIDATION_FAILED, fieldError.getDefaultMessage(), filed);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.VALIDATION_FAILED, null);
    }

    @ExceptionHandler(BusinessBaseException.class)
    public ResponseEntity<ResponseDTO> handleBusinessException(BusinessBaseException ex) {
        log.warn(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ex.getErrorCode(), ex.getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleException(Exception ex) {
        log.error(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.SERVER_ERROR, null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO> handleRuntimeException(RuntimeException ex) {
        log.error(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.SERVER_ERROR, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseDTO> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn(ERROR_MESSAGE, ex.getMessage(), ex);
        return createErrorResponse(ErrorCode.FILE_SIZE_LIMIT_EXCEEDED, null);
    }
}