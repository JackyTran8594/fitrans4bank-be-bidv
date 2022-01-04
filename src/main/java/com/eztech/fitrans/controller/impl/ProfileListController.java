package com.eztech.fitrans.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eztech.fitrans.controller.ProfileListApi;
import com.eztech.fitrans.dto.response.ProfileListDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.ProfileListService;

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
@RequestMapping("/api/profileList")
public class ProfileListController extends BaseController implements ProfileListApi {

    @Autowired
    private ProfileListService ProfileListService;

    @Override
    @GetMapping("")
    public Page<ProfileListDTO> getList(@RequestParam Map<String, Object> mapParam,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        // TODO Auto-generated method stub
        List<ProfileListDTO> listData = new ArrayList<ProfileListDTO>();
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
        }
        mapParam.put("pageNumber", pageNumber);
        mapParam.put("pageSize", pageSize);
        Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
        // if (mapParam.isEmpty()) {
        listData = ProfileListService.search(mapParam);
        // listData = ProfileListService.findAll();
        // }
        Long total = ProfileListService.count(mapParam);
        return new PageImpl<>(listData, pageable, total);
    }

    @Override
    @GetMapping("/{id}")
    public ProfileListDTO getById(@PathVariable(value = "id") Long id) {
        // TODO Auto-generated method stub
        ProfileListDTO dto = ProfileListService.findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("ProfileList" + id + "not found");
        }
        return dto;
    }

    @Override
    @PostMapping("")
    public ProfileListDTO create(@RequestBody ProfileListDTO dto) {
        // TODO Auto-generated method stub
        System.out.println(dto);

        return ProfileListService.save(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProfileListDTO update(@PathVariable(value = "id") Long id, @RequestBody ProfileListDTO dto) {
        // TODO Auto-generated method stub
        dto.setId(id);
        return ProfileListService.save(dto);
    }

    @Override
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable(value = "id") Long id) {
        // TODO Auto-generated method stub
        ProfileListService.deleteById(id);
        return true;
    }

    @PostMapping("/deleteList")
    public Boolean deleteList(@RequestBody List<ProfileListDTO> listData) {
        // TODO Auto-generated method stub
        for (var item : listData) {
            ProfileListService.deleteById(item.getId());
        }
        return true;
    }

}
