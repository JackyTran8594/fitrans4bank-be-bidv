package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum DashboardCardCTTypeEnum {
    G2(2,"Hồ sơ dự kiến xử lý (chưa tới rổ chung)"),
    G3(3,"Hồ sơ đang ở rổ chung trong ngày"),
    G4(4,"Hồ sơ đang xử lý trong hạn"),
    Q5(5,"Hồ sơ đang xử lý quá hạn"),
    Q6(6,"Hồ sơ đã xử lý hoàn thành trong ngày"),
    Q7(7,"Hồ sơ đã xử lý hoàn thành lũy kế"),
    Q8(8,"Hồ sơ giao dịch khách hàng trả lại trong ngày"),

    UNKNOWN(-1,"UNKNOWN");

    private int value;
    private String name;

    private DashboardCardCTTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static DashboardCardCTTypeEnum of(int value) {
        return Stream.of(DashboardCardCTTypeEnum.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElse(DashboardCardCTTypeEnum.UNKNOWN);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
