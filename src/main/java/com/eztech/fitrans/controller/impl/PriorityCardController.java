package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.PriorityCardApi;
import com.eztech.fitrans.dto.response.PriorityCardDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.PriorityCardService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/priorityCard")
public class PriorityCardController extends BaseController implements PriorityCardApi {

  @Autowired
  private PriorityCardService service;

  @Override
  @GetMapping("")
  public Page<PriorityCardDTO> getList(
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
    List<PriorityCardDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public PriorityCardDTO getById(@PathVariable(value = "id") Long id) {
    PriorityCardDTO dto = service.findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("PriorityCard " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  public PriorityCardDTO create(@RequestBody PriorityCardDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  // @PreAuthorize("hasPriorityCard('PriorityCard_ADMIN')")
  public PriorityCardDTO update(@PathVariable(value = "id") Long id, @RequestBody PriorityCardDTO item) {
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
  @DeleteMapping("")
  public Boolean delete(@RequestParam(value = "ids") List<Long> ids) {
    service.deleteById(ids);
    return true;
  }

}