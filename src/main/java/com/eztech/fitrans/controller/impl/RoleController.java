package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.RoleApi;
import com.eztech.fitrans.dto.response.RoleDTO;
import com.eztech.fitrans.dto.response.RoleListDTO;
import com.eztech.fitrans.dto.response.RoleTreeDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.RoleService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController extends BaseController implements RoleApi {

  @Autowired
  private RoleService service;

  @Override
  @GetMapping("")
  public Page<RoleDTO> getList(
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
    List<RoleDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public RoleDTO getById(@PathVariable(value = "id") Long id) {
    RoleDTO dto = service.findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Role " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  public RoleDTO create(@RequestBody RoleDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public RoleDTO update(@PathVariable(value = "id") Long id, @RequestBody RoleDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @Override
  @GetMapping("/tree")
  public List<RoleTreeDTO> treeAll() {
    return service.treeRole();
  }

}