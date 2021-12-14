package com.eztech.fitrans.common;

public enum CamundaProcess {
    CREATE_NETWORK_PRODUCT("Create_Network_Product"),
    CAMUNDA_TEST("camunda_test_"),
    ;
    private String name;

    CamundaProcess(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
