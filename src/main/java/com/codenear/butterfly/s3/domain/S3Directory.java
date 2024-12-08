package com.codenear.butterfly.s3.domain;

import lombok.Getter;

@Getter
public enum S3Directory {
    TEST("test/"),
    PROFILE_IMAGE("image/"),
    PRODUCT_IMAGE("/");

    private final String value;

    S3Directory(String value) {
        this.value = value;
    }
}
