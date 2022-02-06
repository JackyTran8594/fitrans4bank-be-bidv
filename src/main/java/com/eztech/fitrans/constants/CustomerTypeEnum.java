package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum CustomerTypeEnum {
    NORMAL(1,"Khách hàng thông thường"),
    VIP(2,"VIP"),
    UNKNOWN(-1,"UNKNOWN");

    private int value;
    private String name;

    private CustomerTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static CustomerTypeEnum of(int value) {
        return Stream.of(CustomerTypeEnum.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElse(CustomerTypeEnum.UNKNOWN);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
