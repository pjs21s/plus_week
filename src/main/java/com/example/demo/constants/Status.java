package com.example.demo.constants;

public enum Status {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    CANCELED("CANCELED"),
    EXPIRED("EXPIRED");

    private String value;

    Status(String str) {
        this.value = str;
    }

    public String getValue() {
        return value;
    }
}
