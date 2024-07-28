package com.ussd.enumclass;

public enum UssdPageType {
    INPUT("INPUT"),
    OPTION("OPTION"),
    DIALOG("DIALOG");

    private final String value;

    UssdPageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
