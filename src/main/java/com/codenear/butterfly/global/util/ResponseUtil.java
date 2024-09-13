package com.codenear.butterfly.global.util;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    private static final String DEFAULT_SUCCESS_MESSAGE = "성공적으로 처리 완료 되었습니다.";
    private static final HttpStatus DEFAULT_SUCCESS_STATUS = HttpStatus.OK;

    public static ResponseEntity<ResponseDTO> createSuccessResponse(Object body) {
        return createSuccessResponse(DEFAULT_SUCCESS_STATUS , DEFAULT_SUCCESS_MESSAGE, body);
    }

    public static ResponseEntity<ResponseDTO> createSuccessResponse(String message, Object body) {
        return createSuccessResponse(DEFAULT_SUCCESS_STATUS , message, body);
    }

    public static ResponseEntity<ResponseDTO> createSuccessResponse(HttpStatus status, String message, Object body) {
        return ResponseEntity
                .status(status)
                .body(new ResponseDTO(status.value(), message, body));
    }

    public static ResponseEntity<ResponseDTO> createErrorResponse(ErrorCode errorCode, Object body) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ResponseDTO(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        body)
                );
    }
}
