package com.eztech.fitrans.ecommerce.DTO;

public class OrderTypeStatisticDTO {
    private int orderTypeId;
    private int count;

    public int getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(int orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
