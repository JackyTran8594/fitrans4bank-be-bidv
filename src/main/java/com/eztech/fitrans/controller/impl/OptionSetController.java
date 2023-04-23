package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.OptionSetApi;
import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetMasterData;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.OptionSetService;
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
@RequestMapping("/api/option-set")
public class OptionSetController extends BaseController implements OptionSetApi {

  @Autowired
  private OptionSetService service;

  @Override
  @GetMapping("")
  public Page<OptionSetDTO> getList(
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
    List<OptionSetDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public OptionSetDTO getById(@PathVariable(value = "id") Long id) {
    OptionSetDTO dto = service.detailById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("OptionSet " + id + " not found");
    }
    return dto;
  }

  @Override
  @GetMapping("detail/{code}")
  public OptionSetDTO getByCode(@PathVariable(value = "code") String code) {
    OptionSetDTO dto = service.detailByCode(code);
    if (dto == null) {
      throw new ResourceNotFoundException("OptionSet " + code + " not found");
    }
    return dto;
  }

  @Override
  @GetMapping("list/{code}")
  public List<OptionSetValueDTO> listByCode(@PathVariable(value = "code") String code) {
    return service.listByCode(code);
  }

  @Override
  @PostMapping("")
  public OptionSetDTO create(@RequestBody OptionSetDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  // @PreAuthorize("hasRole('ROLE_ADMIN')")
  public OptionSetDTO update(@PathVariable(value = "id") Long id, @RequestBody OptionSetDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @GetMapping("/getMasterData") 
  public List<OptionSetMasterData> getMasterData() {
    
    return service.getOptionSetMasterData();
  }
}