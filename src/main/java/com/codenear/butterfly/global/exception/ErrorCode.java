package com.codenear.butterfly.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PAYMENT_REQUIRED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // 400 (BAD_REQUEST_ERROR)
    VALIDATION_FAILED(40000, "요청 데이터 검증 실패", BAD_REQUEST),
    NICKNAME_GENERATION_FAILED(40001, "닉네임 생성 중 오류 발생", BAD_REQUEST),
    INVALID_NICKNAME_FORMAT(40002, "잘못된 닉네임 형식", BAD_REQUEST),
    FORBIDDEN_NICKNAME(40003, "사용할 수 없는 닉네임입니다.", BAD_REQUEST),
    CERTIFY_CODE_MISMATCH(40004, "인증 번호가 일치하지 않습니다.", BAD_REQUEST),
    CERTIFY_CODE_EXPIRED(40005, "인증 번호 입력 시간이 초과되었습니다. 다시 시도해 주세요.", BAD_REQUEST),
    FILE_SIZE_LIMIT_EXCEEDED(40006, "업로드 파일 크기가 초과되었습니다.", BAD_REQUEST),
    INSUFFICIENT_STOCK(40007, "해당 상품의 재고가 부족합니다.", BAD_REQUEST),
    INVALID_PAYMENT_METHOD(40008, "해당 결제 수단을 사용할 수 없습니다.", BAD_REQUEST),
    INVALID_APPROVE_DATA_TYPE(40009, "지원하지 않는 데이터 타입 입니다.", BAD_REQUEST),
    INVALID_POINT_VALUE(40010, "잔여 포인트보다 사용 포인트가 많습니다.", BAD_REQUEST),

    // 401 (UNAUTHORIZED)
    NULL_JWT_ACCESS_TOKEN(40100, "(Access) 토큰이 존재하지 않습니다.", UNAUTHORIZED),
    EXPIRED_JWT_ACCESS_TOKEN(40100, "(Access) 토큰이 만료 되었습니다.", UNAUTHORIZED),
    INVALID_JWT_ACCESS_SIGNATURE(40100, "(Access) 유효하지 않은 JWT 서명입니다.", UNAUTHORIZED),
    NULL_JWT_REFRESH_TOKEN(40101, "(Refresh) 토큰이 존재하지 않습니다.", UNAUTHORIZED),
    EXPIRED_JWT_REFRESH_TOKEN(40101, "(Refresh) 토큰이 만료 되었습니다.", UNAUTHORIZED),
    INVALID_JWT_REFRESH_SIGNATURE(40101, "(Refresh) 유효하지 않은 JWT 서명입니다.", UNAUTHORIZED),
    BLACKLIST_JWT_REFRESH_TOKEN(40101, "(Refresh) 사용이 금지된 토큰입니다.", UNAUTHORIZED),

    // 402 (PAYMENT_REQUIRED)
    PAY_FAILED(40200, "결제가 실패하였습니다.", PAYMENT_REQUIRED),
    PAYMENT_REDIRECT_FAILED(40201, "페이지 이동에 실패하였습니다", PAYMENT_REQUIRED),
    PAYMENT_NOT_FOUND_PROVIDER(40202, "지원하지 않는 결제수단입니다.", PAYMENT_REQUIRED),

    // 403 (FORBIDDEN)
    INVALID_EMAIL_OR_PASSWORD(40300, "아이디 혹은 비밀번호가 틀렸습니다.", FORBIDDEN),

    // 404 (NOT_FOUND),
    PRODUCT_NOT_FOUND(40400, "등록된 상품이 없습니다.", NOT_FOUND),
    ADDRESS_NOT_FOUND(40401, "해당 주소가 없습니다.", NOT_FOUND),
    MEMBER_NOT_FOUND(40402, "등록되지 않은 회원입니다.", NOT_FOUND),
    ORDER_NOT_FOUND(40403, "해당 주문이 없습니다.", NOT_FOUND),
    NOTIFY_MESSAGE_NOT_FOUND(40404, "찾을 수 없는 알림 메시지 입니다.", NOT_FOUND),
    MEMBER_NOT_FOUND_BY_PHONE(40405, "일치하는 회원 정보가 없습니다.", NOT_FOUND),
    MEMBER_NOT_FOUND_BY_EMAIL(40406, "일치하는 회원 정보가 없습니다.", NOT_FOUND),
    MEMBER_NOT_FOUND_BY_EMAIL_AND_PLATFORM(40407, "일치하는 회원 정보가 없습니다.", NOT_FOUND),
    PRODUCT_NOT_SELECTED(40408, "처리할 주문이 선택되지 않았습니다.", NOT_FOUND),

    // 409 (CONFLICT)
    EMAIL_ALREADY_IN_USE(40900, "이메일이 중복되었습니다.", CONFLICT),
    DUPLICATE_FAVORITE(40901, "이미 찜 목록에 추가된 상품입니다.", CONFLICT),
    NICKNAME_ALREADY_IN_USE(40902, "이미 사용 중인 닉네임입니다.", CONFLICT),
    PHONE_NUMBER_ALREADY_USE(40903, "해당 전화번호는 다른 계정에서 이미 사용 중입니다. 만약 어떤 계정에 연결되어 있는지 모르겠다면, 문의하기를 통해 남겨주시면 빠르게 도와드리겠습니다! \uD83D\uDE0A", CONFLICT),
    WITHDRAWN_ID(40903, "해당 계정은 탈퇴한 계정입니다.", CONFLICT),

    // 500 (INTERNAL_SERVER_ERROR)
    SERVER_ERROR(500, "이용에 불편을 드려 죄송합니다. 현재 시스템 오류가 발생했습니다. 잠시 후 다시 시도 및 고객 문의 바랍니다.", INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}