package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum ProfileTypeEnum {
    VAY(1,"Cho vay");

    private int type;
    private String name;

    private ProfileTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static ProfileTypeEnum of(int type) {
        return Stream.of(ProfileTypeEnum.values())
                .filter(p -> p.getType() == type)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
