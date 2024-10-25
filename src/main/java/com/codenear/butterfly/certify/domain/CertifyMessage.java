package com.codenear.butterfly.certify.domain;

public enum CertifyMessage {
    SMS("나비 본인 확인 인증번호 [%s]을(를) 입력해 주세요.");

    private final String message;

    CertifyMessage(String message) {
        this.message = message;
    }

    public String getMessage(String number) {
        return String.format(message, number);
    }
}
