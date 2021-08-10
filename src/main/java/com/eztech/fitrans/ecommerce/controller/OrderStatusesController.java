package com.eztech.fitrans.ecommerce.controller;

import com.eztech.fitrans.ecommerce.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/order-statuses")
public class OrderStatusesController {
    private OrderStatusService orderStatusService;

    @Autowired
    OrderStatusesController(OrderStatusService orderStatusService) {
        this.orderStatusService = orderStatusService;
    }

    @GetMapping("")
    public ResponseEntity<List<String>> getAll() {
        return ok(orderStatusService.getPossibleStatusesList());
    }
}
