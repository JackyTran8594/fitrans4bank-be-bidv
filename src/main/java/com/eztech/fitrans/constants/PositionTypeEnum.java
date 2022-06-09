package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum PositionTypeEnum {
    CHUYENVIEN(1,"CHUYENVIEN"),
    TRUONGPHONG(2,"TRUONGPHONG"),
    LANHDAO(2,"LANHDAO"),
    UNKNOWN(-1,"UNKNOWN");

    private int value;
    private String name;

    private PositionTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static PositionTypeEnum of(int value) {
        return Stream.of(PositionTypeEnum.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElse(PositionTypeEnum.UNKNOWN);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
