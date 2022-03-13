package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum ProfileStateEnum {
    NOT_YET(0,"Chưa bàn giao"),
    TRANFERED(1, "Đã chuyển"),
    RECEIVED(2, "Đã nhận hồ sơ"),
    ASSIGNED(3, "Đã phân công"),
    WAITING(4, "Chờ xử lý"),
    PROCESSING(5, "Đang xử lý"),
    ADDITIONAL(6, "Cần bổ sung"),
    FINISHED(7,"Đã hoàn thành"),
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
