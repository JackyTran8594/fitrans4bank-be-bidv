package com.eztech.fitrans.ecommerce.controller;

import com.eztech.fitrans.ecommerce.DTO.TrafficAggregationDTO;
import com.eztech.fitrans.ecommerce.service.TrafficAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.eztech.fitrans.ecommerce.Constants.DEFAULT_FILTER;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/traffic-aggregated")
public class TrafficAggregatedController {
    private TrafficAggregationService trafficAggregationService;

    @Autowired
    public TrafficAggregatedController(TrafficAggregationService trafficAggregationService) {
        this.trafficAggregationService = trafficAggregationService;
    }

    @GetMapping("")
    public ResponseEntity<List<TrafficAggregationDTO>> trafficStatistics(String filter) {

        String trafficStatisticsFilter = filter == null ? DEFAULT_FILTER : filter;
        return ok(trafficAggregationService.getDataForTable(trafficStatisticsFilter));
    }
}
