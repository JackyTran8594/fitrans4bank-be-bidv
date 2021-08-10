package com.eztech.fitrans.ecommerce.entity.statistic;

public class OrderTypeStatistic {
    private int orderTypeId;
    private int count;

    public OrderTypeStatistic(int orderTypeId, int count) {
        this.orderTypeId = orderTypeId;
        this.count = count;
    }

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
