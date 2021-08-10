package com.eztech.fitrans.ecommerce.entity.enums;

import java.util.stream.Stream;

public enum MonthsEnum {
    Jan(1), Feb(2), Mar(3), Apr(4), May(5), Jun(6),
    Jul(7), Aug(8), Sep(9), Oct(10), Nov(11), Dec(12);

    private int value;

    MonthsEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String from(int index) {
        return MonthsEnum.values()[index - 1].toString();
    }


    public static String[] names() {
        return Stream.of(MonthsEnum.values()).map(MonthsEnum::name).toArray(String[]::new);
    }
}
