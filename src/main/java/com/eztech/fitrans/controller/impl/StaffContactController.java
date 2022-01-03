package com.eztech.fitrans.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eztech.fitrans.controller.StaffContactApi;
import com.eztech.fitrans.dto.response.StaffContactDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.StaffContactService;

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
@RequestMapping("/api/staffContact")
public class StaffContactController extends BaseController implements StaffContactApi {

    @Autowired
    private StaffContactService staffContactService;

    @Override
    @GetMapping("")
    public Page<StaffContactDTO> getList(@RequestParam Map<String, Object> mapParam,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        // TODO Auto-generated method stub
        List<StaffContactDTO> listData = new ArrayList<StaffContactDTO>();
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
        }
        mapParam.put("pageNumber", pageNumber);
        mapParam.put("pageSize", pageSize);
        Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
        // if (mapParam.isEmpty()) {
        listData = staffContactService.search(mapParam);
        // listData = staffContactService.findAll();
        // }
        Long total = staffContactService.count(mapParam);
        return new PageImpl<>(listData, pageable, total);
    }

    @Override
    @GetMapping("/{id}")
    public StaffContactDTO getById(@PathVariable(value = "id") Long id) {
        // TODO Auto-generated method stub
        StaffContactDTO dto = staffContactService.findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("StaffContact" + id + "not found");
        }
        return dto;
    }

    @Override
    @PostMapping("")
    public StaffContactDTO create(@RequestBody StaffContactDTO dto) {
        // TODO Auto-generated method stub
        System.out.println(dto);

        return staffContactService.save(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public StaffContactDTO update(@PathVariable(value = "id") Long id, @RequestBody StaffContactDTO dto) {
        // TODO Auto-generated method stub
        dto.setId(id);
        return staffContactService.save(dto);
    }

    @Override
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable(value = "id") Long id) {
        // TODO Auto-generated method stub
        staffContactService.deleteById(id);
        return true;
    }

    @PostMapping("/deleteList")
    public Boolean deleteList(@RequestBody List<StaffContactDTO> listData) {
        // TODO Auto-generated method stub
        for (var item : listData) {
            staffContactService.deleteById(item.getId());
        }
        return true;
    }

}
