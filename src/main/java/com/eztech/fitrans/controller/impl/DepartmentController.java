package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.DepartmentApi;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Department;
import com.eztech.fitrans.service.DepartmentService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/departments")
public class DepartmentController extends BaseController implements DepartmentApi {

  @Autowired
  private DepartmentService departmentService;

  @Override
  @GetMapping("")
  public Page<DepartmentDTO> getList(
      @RequestParam Map<String, Object> mapParam,
      @RequestParam int pageNumber,
      @RequestParam int pageSize
  ) {
    if(pageNumber > 0){
      pageNumber = pageNumber - 1;
    }
    mapParam.put("pageNumber", pageNumber);
    mapParam.put("pageSize", pageSize);
    Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
    List<DepartmentDTO> listData = departmentService.search(mapParam);
    Long total = departmentService.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public DepartmentDTO getById(@PathVariable(value = "id") Long id) {
    DepartmentDTO dto = departmentService.findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Department " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  public DepartmentDTO create(@RequestBody DepartmentDTO item) {
    return departmentService.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public DepartmentDTO update(@PathVariable(value = "id") Long id,
      @RequestBody DepartmentDTO item) {
    item.setId(id);
    return departmentService.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    departmentService.deleteById(id);
    return true;
  }
}