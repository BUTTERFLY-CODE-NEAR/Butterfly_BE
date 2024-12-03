package com.codenear.butterfly.consent.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConsentType {
    MARKETING("marketing"),
    DELIVERY_NOTIFICATION("none");

    private final String topic;

    public boolean hasTopic() {
        return !topic.equals("none");
    }
}
