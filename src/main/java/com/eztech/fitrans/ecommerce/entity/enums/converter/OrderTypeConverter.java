package com.eztech.fitrans.ecommerce.entity.enums.converter;

import com.eztech.fitrans.ecommerce.entity.enums.OrderTypeEnum;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static com.eztech.fitrans.ecommerce.Constants.DEFAULT_ORDER_TYPE;

@Converter
@SuppressWarnings("checkstyle:magicnumber")
public class OrderTypeConverter implements AttributeConverter<OrderTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(OrderTypeEnum attribute) {
        attribute = attribute == null ? DEFAULT_ORDER_TYPE : attribute;
        return attribute.getValue();
    }

    @Override
    public OrderTypeEnum convertToEntityAttribute(String dbData) {
        dbData = dbData == null ? DEFAULT_ORDER_TYPE.getValue() : dbData;
        return OrderTypeEnum.valueOf(dbData.toUpperCase());
    }
}
