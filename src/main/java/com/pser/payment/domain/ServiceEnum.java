package com.pser.payment.domain;

import lombok.Getter;

@Getter
public enum ServiceEnum {
    RESERVATION("reservation"),
    DEPOSIT("deposit"),
    AUCTION("auction");

    private final String topicPrefix;

    ServiceEnum(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
}
