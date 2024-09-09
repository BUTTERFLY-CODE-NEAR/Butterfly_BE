package com.codenear.butterfly.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {
    KAKAO,
    GOOGLE,
    CODENEAR;

    @JsonCreator // Json -> Java
    public static Platform fromString(String key) {
        try {
            return Platform.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @JsonValue // Java -> Json
    public String toValue() {
        return this.name();
    }
}
