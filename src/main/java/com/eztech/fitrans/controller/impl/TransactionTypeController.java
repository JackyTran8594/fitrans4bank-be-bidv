package com.eztech.fitrans.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eztech.fitrans.controller.TransactionTypeApi;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.TransactionTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/transactionType")
public class TransactionTypeController extends BaseController implements TransactionTypeApi {

    @Autowired
    private TransactionTypeService TransactionTypeService;

    @Override
    @GetMapping("")
    public Page<TransactionTypeDTO> getList(@RequestParam Map<String, Object> mapParam,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        // TODO Auto-generated method stub
        List<TransactionTypeDTO> listData = new ArrayList<TransactionTypeDTO>();
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
        }
        mapParam.put("pageNumber", pageNumber);
        mapParam.put("pageSize", pageSize);
        Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
        // if (mapParam.isEmpty()) {
        listData = TransactionTypeService.search(mapParam);
        // listData = TransactionTypeService.findAll();
        // }
        Long total = TransactionTypeService.count(mapParam);
        return new PageImpl<>(listData, pageable, total);
    }

    @Override
    @GetMapping("/{id}")
    public TransactionTypeDTO getById(@PathVariable(value = "id") Long id) {
        // TODO Auto-generated method stub
        TransactionTypeDTO dto = TransactionTypeService.findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("TransactionType" + id + "not found");
        }
        return dto;
    }

    @Override
    @PostMapping("")
    public TransactionTypeDTO create(@RequestBody TransactionTypeDTO dto) {
        // TODO Auto-generated method stub
        System.out.println(dto);

        return TransactionTypeService.save(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TransactionTypeDTO update(@PathVariable(value = "id") Long id, @RequestBody TransactionTypeDTO dto) {
        // TODO Auto-generated method stub
        dto.setId(id);
        return TransactionTypeService.save(dto);
    }

    @Override
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable(value = "id") Long id) {
        // TODO Auto-generated method stub
        TransactionTypeService.deleteById(id);
        return true;
    }

    @PostMapping("/deleteList")
    public Boolean deleteList(@RequestBody List<TransactionTypeDTO> listData) {
        // TODO Auto-generated method stub
        for (var item : listData) {
            TransactionTypeService.deleteById(item.getId());
        }
        return true;
    }

}
