package com.eztech.fitrans.ecommerce.service;

import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusService {
    public List<String> getPossibleStatusesList() {
        return OrderStatusEnum.getPossibleStatusesAsList();
    }
}
