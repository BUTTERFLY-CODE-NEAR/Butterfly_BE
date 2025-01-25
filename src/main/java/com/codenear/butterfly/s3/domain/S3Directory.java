package com.codenear.butterfly.s3.domain;

import lombok.Getter;

@Getter
public enum S3Directory {
    TEST("test/"),
    PROFILE_IMAGE("profile/image"),
    PRODUCT_IMAGE("product/image");

    private final String value;

    S3Directory(String value) {
        this.value = value;
    }
}
