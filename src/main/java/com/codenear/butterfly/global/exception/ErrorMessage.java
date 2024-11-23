package com.codenear.butterfly.global.exception;

public enum ErrorMessage {
    INVALID_ID("해당 ID를 찾을 수 없습니다 : %d");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }

    public String get(Object... value) {
        return String.format(message, value);
    }
}
