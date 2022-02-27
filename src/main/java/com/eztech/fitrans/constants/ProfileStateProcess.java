package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum ProfileStateProcess {

    EXPEDITE(0,"Cần gấp"),
    PRE_EXPIRE(1, "Sắp hết hạn"),
    EXPIRED(2, "Quá hạn xử lý"),
    UNKNOWN(-1, "UNKNOWN");

    private int value;
    private String name;

    private ProfileStateProcess(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static ProfileStateProcess of(int value) {
        return Stream.of(ProfileStateProcess.values()).filter(p -> p.getValue() == value).findFirst()
                .orElse(ProfileStateProcess.UNKNOWN);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
