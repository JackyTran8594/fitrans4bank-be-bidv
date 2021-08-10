package com.eztech.fitrans.ecommerce.service;

import com.eztech.fitrans.ecommerce.entity.enums.OrderTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTypeService {
    public List<String> getList() {
        return OrderTypeEnum.asList();
    }
}
