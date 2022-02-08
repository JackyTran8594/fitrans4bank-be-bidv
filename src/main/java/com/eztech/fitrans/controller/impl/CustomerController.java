package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.CustomerApi;
import com.eztech.fitrans.dto.response.CustomerDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.CustomerService;
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
@RequestMapping("/api/customers")
public class CustomerController extends BaseController implements CustomerApi {

  @Autowired
  private CustomerService service;

  @Override
  @GetMapping("/all")
  public List<CustomerDTO> listAll() {
    return service.findAll();
  }

  @Override
  @GetMapping("")
  public Page<CustomerDTO> getList(
      @RequestParam Map<String, Object> mapParam,
      @RequestParam int pageNumber,
      @RequestParam int pageSize) {
    if (pageNumber > 0) {
      pageNumber = pageNumber - 1;
    }
    mapParam.put("pageNumber", pageNumber);
    mapParam.put("pageSize", pageSize);
    Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
    List<CustomerDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public CustomerDTO getById(@PathVariable(value = "id") Long id) {
    CustomerDTO dto = service.findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Customer " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  public CustomerDTO create(@RequestBody CustomerDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public CustomerDTO update(@PathVariable(value = "id") Long id, @RequestBody CustomerDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @GetMapping("/Cif/{cif}")
  public List<CustomerDTO> getByCif(@PathVariable(value = "cif") String cif) {
    List<CustomerDTO> dto = service.findByCif(cif);
    if (dto == null) {
      throw new ResourceNotFoundException("Customer " + cif + "not found");
    }
    return dto;
  }
}