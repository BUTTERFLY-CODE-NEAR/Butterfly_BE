package com.codenear.butterfly.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // 400 (BAD_REQUEST_ERROR)
    VALIDATION_FAILED(40000, "요청 데이터 검증 실패", HttpStatus.BAD_REQUEST),
    NICKNAME_GENERATION_FAILED(40001, "닉네임 생성 중 오류 발생", HttpStatus.BAD_REQUEST),
    INVALID_NICKNAME_FORMAT(40002, "잘못된 닉네임 형식", HttpStatus.BAD_REQUEST),
    INVALID_PLATFORM(40003, "제공하지 않는 플랫폼입니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN_NICKNAME(40004, "사용할 수 없는 닉네임입니다.", HttpStatus.BAD_REQUEST),

    // 401 (UNAUTHORIZED)
    NULL_JWT_ACCESS_TOKEN(40100, "(Access) 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_ACCESS_TOKEN(40100, "(Access) 토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_ACCESS_SIGNATURE(40100, "(Access) 유효하지 않은 JWT 서명입니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND_FOR_TOKEN(40101, "(Access) 토큰에 알맞는 회원 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    NULL_JWT_REFRESH_TOKEN(40101, "(Refresh) 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_REFRESH_TOKEN(40101, "(Refresh) 토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_REFRESH_SIGNATURE(40101, "(Refresh) 유효하지 않은 JWT 서명입니다.", HttpStatus.UNAUTHORIZED),
    BLACKLIST_JWT_REFRESH_TOKEN(40101, "(Refresh) 사용이 금지된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_EMAIL_OR_PASSWORD(40102, "아이디 혹은 비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED),

    // 409 (CONFLICT)
    EMAIL_ALREADY_IN_USE(40900, "이메일이 중복되었습니다.", HttpStatus.CONFLICT),

    // 500 (INTERNAL_SERVER_ERROR)
    SERVER_ERROR(500, "서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
