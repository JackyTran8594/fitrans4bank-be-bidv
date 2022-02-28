package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum ProfileStateEnum {
    NOT_YET(0,"Chưa bàn giao"),
    TRANFERED(1, "Đã chuyển"),
    DELEVERIED(2, "Đã bàn giao"),
    WAITING(3, "Chờ xử lý"),
    PROCESSING(4, "Đang xử lý"),
    ADDITIONAL(5, "Cần bổ sung"),
    FINISHED(6,"Đã hoàn thành"),
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
