package com.eztech.fitrans.ecommerce.entity.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OrderTypeEnum {
    SOFAS("Sofas"),
    FURNITURE("Furniture"),
    LIGHTNING("Lightning"),
    TABLES("Tables"),
    TEXTILES("Textiles");

    private String value;

    OrderTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> asList() {
        return Stream.of(OrderTypeEnum.values())
                .map(OrderTypeEnum::getValue)
                .collect(Collectors.toList());
    }
}
