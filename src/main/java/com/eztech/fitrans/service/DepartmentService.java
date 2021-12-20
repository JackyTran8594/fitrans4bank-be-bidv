package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.DepartmentDTO;
import java.util.List;
import java.util.Map;

public interface DepartmentService {

  DepartmentDTO save(DepartmentDTO product);

  void deleteById(Long id);

  DepartmentDTO findById(Long id);

  List<DepartmentDTO> findAll();

  List<DepartmentDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);
}
