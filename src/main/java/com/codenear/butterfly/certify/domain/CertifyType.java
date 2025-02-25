package com.codenear.butterfly.certify.domain;

public enum CertifyType {
    CERTIFY_PHONE("CERTIFY_CODE: %s"),
    CERTIFY_EMAIL("CERTIFY_CODE: %s");

    private final String redisKey;

    CertifyType(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getRedisKey(String phoneNumber) {
        return String.format(redisKey, phoneNumber);
    }
}
