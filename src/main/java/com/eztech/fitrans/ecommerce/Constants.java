package com.eztech.fitrans.ecommerce;

import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import com.eztech.fitrans.ecommerce.entity.enums.OrderTypeEnum;

public class Constants {
    private Constants() {
    }

    public static final int YEARS_BEFORE = 9;
    public static final int DAYS_IN_WEEK = 7;
    public static final int MONTHS_IN_YEAR = 12;
    public static final int DAYS_IN_DECEMBER = 31;
    public static final int LAST_HOUR = 23;
    public static final int LAST_MINUTE = 59;
    public static final int PRECISION = 8;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int AMOUNT_OF_PROCENTS = 100;
    public static final int SIMPLE_NUMBER = 31;
    public static final String DEFAULT_FILTER = "year";
    public static final String DEFAULT_AGGREGATION = "month";
    public static final String COUNTRY = "country";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    public static final String DATE = "date";
    public static final String DATE_FORMAT = "DD/MM/YYYY";
    public static final OrderTypeEnum DEFAULT_ORDER_TYPE = OrderTypeEnum.SOFAS;
    public static final OrderStatusEnum DEFAULT_ORDER_STATUS = OrderStatusEnum.PAYMENT;
}
