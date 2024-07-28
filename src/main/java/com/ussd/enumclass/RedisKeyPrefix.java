package com.ussd.enumclass;

public enum RedisKeyPrefix {
    SESSION("WA_SESS:"),
    CUSTOMER_DATA("WA_CUSTOMER_DATA"),
    COLLECTED_DATA("WA_COLLECTED_DATA:"),
    EMPLOYERS_DATA("WA_EMPLOYERS_DATA:"),
    LOAN_PURPOSE_DATA("WA_LOAN_PURPOSE_DATA:"),
    PASSWORD_LESS_CUSTOMER_DATA("WA_PASSWORD_LESS_CUSTOMER_DATA");

    private final String value;

    RedisKeyPrefix(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
