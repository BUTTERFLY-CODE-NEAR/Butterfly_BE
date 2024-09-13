package com.codenear.butterfly.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // 400 (BAD_REQUEST ERROR)
    VALIDATION_FAILED(40000, "요청 데이터 검증 실패", HttpStatus.BAD_REQUEST),

    // 401 (UNAUTHORIZED)
    NULL_JWT_ACCESS_TOKEN(40100, "(Access) 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_ACCESS_TOKEN(40100, "(Access) 토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_ACCESS_SIGNATURE(40100, "(Access) 유효하지 않은 JWT 서명입니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND_FOR_TOKEN(40101, "(Access) 토큰에 알맞는 회원 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    NULL_JWT_REFRESH_TOKEN(40101, "(Refresh) 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_REFRESH_TOKEN(40101, "(Refresh) 토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_REFRESH_SIGNATURE(40101, "(Refresh) 유효하지 않은 JWT 서명입니다.", HttpStatus.UNAUTHORIZED),
    BLACKLIST_JWT_REFRESH_TOKEN(40101, "(Refresh) 사용이 금지된 토큰입니다.", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
