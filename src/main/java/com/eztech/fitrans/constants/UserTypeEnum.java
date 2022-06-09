package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum UserTypeEnum {
    ADMIN(1,"admin"),
    USER(2,"user"),
    UNKNOWN(-1,"UNKNOWN");

    private int value;
    private String name;

    private UserTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static UserTypeEnum of(int value) {
        return Stream.of(UserTypeEnum.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElse(UserTypeEnum.UNKNOWN);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
