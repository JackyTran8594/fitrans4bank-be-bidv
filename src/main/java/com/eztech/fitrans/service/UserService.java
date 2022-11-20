package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.UserDTO;
import java.util.List;
import java.util.Map;

public interface UserService {

  UserDTO save(UserDTO product);

  void deleteById(Long id);

  void deleteById(List<Long> ids);

  UserDTO findById(Long id);

  List<UserDTO> findAll();

  List<UserDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);

  UserDTO findByUsername(String username);

  List<UserDTO> findByDepartmentid(Long departmentId);

  List<UserDTO> findByCode(String departmentCode);

  Integer getNumberOfPriorityByUsername(String username);
	

}
