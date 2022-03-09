package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.MenuRoleTreeDTO;
import com.eztech.fitrans.dto.response.RoleDTO;
import com.eztech.fitrans.dto.response.RoleTreeDTO;
import com.eztech.fitrans.model.RoleList;

import java.util.List;
import java.util.Map;

public interface RoleService {

  RoleDTO save(RoleDTO product);

  void deleteById(Long id);

  RoleDTO findById(Long id);

  List<RoleDTO> findAll();

  List<RoleDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);

  List<RoleTreeDTO> treeRole();

  List<RoleTreeDTO> menuRole();

  RoleList findByCode(String code);
}
