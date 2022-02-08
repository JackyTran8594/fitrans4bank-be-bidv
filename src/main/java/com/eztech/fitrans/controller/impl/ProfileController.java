package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.ProfileApi;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import java.io.File;

import java.io.FileOutputStream;
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
@RequestMapping("/api/profiles")
public class ProfileController extends BaseController implements ProfileApi {

  @Autowired
  private ProfileService service;

  @Override
  @GetMapping("")
  public Page<ProfileDTO> getList(
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
    List<ProfileDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public ProfileDTO getById(@PathVariable(value = "id") Long id) {
    ProfileDTO dto = service.detailById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Profile " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ProfileDTO create(@RequestBody ProfileDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ProfileDTO update(@PathVariable(value = "id") Long id, @RequestBody ProfileDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @PostMapping("/exportDoc")
  public String exportDoc(@RequestBody ProfileDTO item) {
    String strBase64 = service.exportDocument();
    return strBase64;
  }

  
}