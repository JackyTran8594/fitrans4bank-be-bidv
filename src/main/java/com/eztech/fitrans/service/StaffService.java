package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.StaffDTO;
import java.util.List;
import java.util.Map;

public interface StaffService {

  StaffDTO save(StaffDTO product);

  void deleteById(Long id);

  StaffDTO findById(Long id);

  List<StaffDTO> findAll();

  List<StaffDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);

 
}
