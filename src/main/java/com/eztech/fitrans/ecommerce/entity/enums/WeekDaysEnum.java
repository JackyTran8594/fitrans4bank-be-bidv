package com.eztech.fitrans.ecommerce.entity.enums;

import java.util.stream.Stream;

public enum WeekDaysEnum {
    Mon(1), Tue(2), Wed(3), Thu(4), Fri(5), Sat(6), Sun(7);

    private int value;

    WeekDaysEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String from(int index) {
        return WeekDaysEnum.values()[index - 1].toString();
    }


    public static String[] names() {
        return Stream.of(WeekDaysEnum.values()).map(WeekDaysEnum::name).toArray(String[]::new);
    }
}
