package com.eztech.fitrans.ecommerce.entity.enums.converter;

import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static com.eztech.fitrans.ecommerce.Constants.DEFAULT_ORDER_STATUS;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatusEnum attribute) {
        attribute = attribute == null ? DEFAULT_ORDER_STATUS : attribute;
        return attribute.getValue();
    }

    @Override
    public OrderStatusEnum convertToEntityAttribute(String dbData) {
        dbData = dbData == null ? DEFAULT_ORDER_STATUS.getValue() : dbData;
        return OrderStatusEnum.valueOf(dbData.toUpperCase());
    }
}
