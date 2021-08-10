package com.eztech.fitrans.ecommerce.entity.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OrderStatusEnum {
    PAYMENT("Payment"),
    CANCELLED("Cancelled"),
    ALL("All");

    private String value;

    OrderStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> asList() {
        return Stream.of(OrderStatusEnum.values())
                .map(OrderStatusEnum::getValue)
                .collect(Collectors.toList());
    }

    public static List<String> getPossibleStatusesAsList() {
        //this method is used to prevent creating order with status ALL
        List<String> possibleStatuses = Stream.of(OrderStatusEnum.values())
                .map(OrderStatusEnum::getValue)
                .collect(Collectors.toList());

        int indexOfLastElement = possibleStatuses.size() - 1;
        possibleStatuses.remove(indexOfLastElement);
        return possibleStatuses;
    }
}
