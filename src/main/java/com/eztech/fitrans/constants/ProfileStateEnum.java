package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum ProfileStateEnum {
    NOT_YET(0,"Chưa bàn giao"),
    DELEVERIED(1, "Đã chuyển"),
    RECEIVED(2, "Đã nhận"),
    RETURNED(3, "Đã trả"),
    PRE_EXPIRE(4,"Sắp hết hạn"),
    EXPIRED(5,"Quá hạn"),
    FINISH(6,"Đã hoàn thành"),
    UNKNOWN(-1,"UNKNOWN");

    private int value;
    private String name;

    private ProfileStateEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static ProfileStateEnum of(int value) {
        return Stream.of(ProfileStateEnum.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElse(ProfileStateEnum.UNKNOWN);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
