package com.codenear.butterfly.certify.domain;

public enum CertifyType {
    REGISTER_PHONE("RESISTER_CODE: %s")
    ;

    private final String redisKey;

    CertifyType(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getRedisKey(String phoneNumber) {
        return String.format(redisKey, phoneNumber);
    }
}
