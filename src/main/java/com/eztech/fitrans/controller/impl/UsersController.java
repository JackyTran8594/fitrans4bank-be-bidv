package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.UserApi;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.UserService;
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
@RequestMapping("/api/users")
public class UsersController extends BaseController implements UserApi {

  @Autowired
  private UserService userService;

  @Override
  @GetMapping("")
  public Page<UserDTO> getList(
      @RequestParam Map<String, Object> mapParam,
      @RequestParam int pageNumber,
      @RequestParam int pageSize
  ) {
    mapParam.put("pageNumber", pageNumber);
    mapParam.put("pageSize", pageSize);
    Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
    List<UserDTO> listData = userService.search(mapParam);
    Long total = userService.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public UserDTO getById(@PathVariable(value = "id") Long id) {
    UserDTO dto = userService.findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("User " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  public UserDTO create(@RequestBody UserDTO item) {
    return userService.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public UserDTO update(@PathVariable(value = "id") Long id, @RequestBody UserDTO item) {
    item.setId(id);
    return userService.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    userService.deleteById(id);
    return true;
  }
}