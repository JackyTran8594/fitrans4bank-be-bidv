package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.LogApi;
import com.eztech.fitrans.dto.response.ActionLogDTO;
import com.eztech.fitrans.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/logs")
public class LogController extends BaseController implements LogApi {

    @Autowired
    private LogService service;

    @Override
    @GetMapping("")
    public Page<ActionLogDTO> getList(
            @RequestParam Map<String, Object> mapParam,
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
        }
        mapParam.put("pageNumber", pageNumber);
        mapParam.put("pageSize", pageSize);
        Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
        List<ActionLogDTO> listData = service.search(mapParam);
        Long total = service.count(mapParam);
        return new PageImpl<>(listData, pageable, total);
    }


    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean delete(@PathVariable(value = "id") Long id) {
        service.deleteById(id);
        return true;
    }

}