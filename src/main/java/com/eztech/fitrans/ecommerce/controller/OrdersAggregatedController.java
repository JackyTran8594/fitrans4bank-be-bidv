package com.eztech.fitrans.ecommerce.controller;

import com.eztech.fitrans.ecommerce.DTO.BaseChartDTO;
import com.eztech.fitrans.ecommerce.DTO.OrderTypeStatisticDTO;
import com.eztech.fitrans.ecommerce.DTO.OrdersSummaryDTO;
import com.eztech.fitrans.ecommerce.service.OrderAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/orders-aggregated")
public class OrdersAggregatedController {
    private OrderAggregationService orderAggregationService;

    @Autowired
    public OrdersAggregatedController(OrderAggregationService orderAggregationService) {
        this.orderAggregationService = orderAggregationService;
    }

    @GetMapping("")
    public ResponseEntity<BaseChartDTO<Long>> getCountDataForChart(String aggregation) {
        if (aggregation == null) {
            aggregation = "year";
        }
        return ok(orderAggregationService.getCountDataForChart(aggregation));
    }

    @GetMapping("/profit")
    public ResponseEntity<BaseChartDTO<Double>> getProfitDataForChart(String aggregation) {
        if (aggregation == null) {
            aggregation = "year";
        }
        return ok(orderAggregationService.getProfitDataForChart(aggregation));
    }

    @GetMapping("/country")
    public ResponseEntity<List<OrderTypeStatisticDTO>> getStatisticByCountry(String countryCode) {
        return ok(orderAggregationService.getStatisticByCountry(countryCode));
    }

    @GetMapping("/summary")
    public ResponseEntity<OrdersSummaryDTO> getOrdersSummaryInfo() {
        return ok(orderAggregationService.getOrdersSummaryInfo());
    }
}
