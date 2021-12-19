package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.util.DataUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.eztech.fitrans.util.DataUtils.camelToSnake;

public class BaseController {
    public PageRequest pageRequest(List<String> sort, Integer size, Integer page) {
        if (CollectionUtils.isEmpty(sort)) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, sort(sort));
    }

    @SuppressWarnings("java:S3776")
    public Sort sort(List<String> sort) {
        if (CollectionUtils.isEmpty(sort)) {
            return null;
        }

        List<Sort.Order> orderList = new ArrayList<>();
        if (sort.get(0).contains(",")) {
            //&sort=code,asc&sort=lastUpdateDate,desc
            String[] tmpArr;
            for (String tmp : sort) {
                tmpArr = tmp.split(",");
                if (tmpArr.length > 1) {
                    if ("asc".equalsIgnoreCase(tmpArr[1])) {
                        orderList.add(Sort.Order.asc(camelToSnake(tmpArr[0])));
                    } else {
                        orderList.add(Sort.Order.desc(camelToSnake(tmpArr[0])));
                    }
                } else {
                    orderList.add(Sort.Order.asc(camelToSnake(tmpArr[0])));
                }
            }
        } else {
            //sort=code,asc
            for (String s : sort) {
                orderList.add(Sort.Order.asc(camelToSnake(s)));
            }
        }
        return Sort.by(orderList);
    }

    public List<String> getSortParam(String sort) {
        if (DataUtils.isNullOrEmpty(sort)) {
            return new ArrayList<>();
        }
        return Arrays.asList(sort.split(";"));
    }
}
