package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.StaffContactApi;
import com.eztech.fitrans.dto.response.StaffContactDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.StaffContactService;
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
@RequestMapping("/api/staffContact")
public class StaffContactController extends BaseController implements StaffContactApi {

    @Autowired
    private StaffContactService staffContactService;

    @Override
    @GetMapping("")
    public Page<StaffContactDTO> getList(@RequestParam Map<String, Object> mapParam,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        List<StaffContactDTO> listData = new ArrayList<StaffContactDTO>();
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
        }
        mapParam.put("pageNumber", pageNumber);
        mapParam.put("pageSize", pageSize);
        Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
        listData = staffContactService.search(mapParam);
        Long total = staffContactService.count(mapParam);
        return new PageImpl<>(listData, pageable, total);
    }

    @Override
    @GetMapping("/{id}")
    public StaffContactDTO getById(@PathVariable(value = "id") Long id) {
        StaffContactDTO dto = staffContactService.findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("StaffContact" + id + "not found");
        }
        return dto;
    }

    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public StaffContactDTO create(@RequestBody StaffContactDTO dto) {
        return staffContactService.save(dto);
    }

    @Override
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public StaffContactDTO update(@PathVariable(value = "id") Long id, @RequestBody StaffContactDTO dto) {
        dto.setId(id);
        return staffContactService.save(dto);
    }

    @Override
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public Boolean delete(@PathVariable(value = "id") Long id) {
        staffContactService.deleteById(id);
        return true;
    }

    @PostMapping("/deleteList")
    // @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public Boolean deleteList(@RequestBody List<StaffContactDTO> listData) {
        for (var item : listData) {
            staffContactService.deleteById(item.getId());
        }
        return true;
    }

    @Override
    @GetMapping("/cif/{cif}")
    public Boolean getByCode(@PathVariable(value = "cif") String cif,
            @RequestParam(value = "id", required = false) Long id) {
        return staffContactService.findByCif(id, cif);
    }

    @GetMapping("/customer/{id}")
    public StaffContactDTO getByCustomerId(@PathVariable(value = "id") Long id) {
        StaffContactDTO dto = staffContactService.findByCustomerId(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Staff " + id + " not found");
        }
        return dto;
    }

}
