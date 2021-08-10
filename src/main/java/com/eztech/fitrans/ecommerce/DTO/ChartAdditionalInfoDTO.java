package com.eztech.fitrans.ecommerce.DTO;

public class ChartAdditionalInfoDTO<T> {
    private String title;
    private T value;

    public ChartAdditionalInfoDTO(T value, String title) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
