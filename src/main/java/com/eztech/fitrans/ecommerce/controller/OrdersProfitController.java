package com.eztech.fitrans.ecommerce.controller;


import com.eztech.fitrans.ecommerce.DTO.BaseChartDTO;
import com.eztech.fitrans.ecommerce.DTO.OrdersProfitDTO;
import com.eztech.fitrans.ecommerce.DTO.ProfitForTwoMonthChartDTO;
import com.eztech.fitrans.ecommerce.service.OrderAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/orders-profit")
public class OrdersProfitController {
    private OrderAggregationService orderAggregationService;

    @Autowired
    public OrdersProfitController(OrderAggregationService orderAggregationService) {
        this.orderAggregationService = orderAggregationService;
    }

    @GetMapping("")
    public ResponseEntity<BaseChartDTO<Integer>> getProfitChartForYear() {
        return ok(orderAggregationService.getProfitChartForYear());
    }

    @GetMapping("/short")
    public ResponseEntity<ProfitForTwoMonthChartDTO> getProfitChartForTwoMonth() {
        return ok(orderAggregationService.getProfitChartForTwoMonth());
    }

    @GetMapping("/summary")
    public ResponseEntity<OrdersProfitDTO> getProfitSummary() {
        return ok(orderAggregationService.getProfitStatistic());
    }
}
