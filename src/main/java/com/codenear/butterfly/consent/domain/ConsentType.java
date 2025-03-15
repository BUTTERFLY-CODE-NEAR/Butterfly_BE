package com.codenear.butterfly.consent.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConsentType {
    MARKETING("marketing"),
    DELIVERY_NOTIFICATION("delivery"),
    CUSTOMER_SUPPORT("support"),
    POINT_BACK("point");

    private final String topic;

    public boolean hasTopic() {
        return !topic.equals("none");
    }
}
