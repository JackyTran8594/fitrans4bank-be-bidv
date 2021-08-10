package com.eztech.fitrans.ecommerce.controller;

import com.eztech.fitrans.ecommerce.DTO.UserActivityDTO;
import com.eztech.fitrans.ecommerce.service.UserActivityAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static com.eztech.fitrans.ecommerce.Constants.DEFAULT_AGGREGATION;
import static org.springframework.http.ResponseEntity.ok;

@Controller
public class UserActivitiesController {

    private UserActivityAggregationService userActivityService;

    @Autowired
    public UserActivitiesController(UserActivityAggregationService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @GetMapping("/user-activity")
    public ResponseEntity<List<UserActivityDTO>> userActivities(String date) {

        String aggregation = date == null ? DEFAULT_AGGREGATION : date;
        return ok(userActivityService.getDataForTable(aggregation));
    }

}
