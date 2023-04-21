package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum DashboardCardCMTypeEnum {
    Q2(2,"Hồ sơ dự kiến xử lý (QHKH mới tạo, QTTD chưa nhận)"),
    Q3(3,"Hồ sơ đang xử lý trong hạn"),
    Q4(4,"Hồ sơ đang xử lý quá hạn"),
    Q5(5,"Hồ sơ đã xử lý hoàn thành trong ngày"),
    Q6(6,"Hồ sơ đã xử lý hoàn thành lũy kế"),
    Q7(7,"Hồ sơ giao dịch khách hàng trả lại trong ngày"),
    UNKNOWN(-1,"UNKNOWN"); 

    private int value;
    private String name;

    private DashboardCardCMTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static DashboardCardCMTypeEnum of(int value) {
        return Stream.of(DashboardCardCMTypeEnum.values())
                .filter(p -> p.getValue() == value)
                .findFirst()
                .orElse(DashboardCardCMTypeEnum.UNKNOWN);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
