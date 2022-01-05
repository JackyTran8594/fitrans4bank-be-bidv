package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;

import java.util.List;
import java.util.Map;

public interface OptionSetService {

  OptionSetDTO save(OptionSetDTO product);

  void deleteById(Long id);

  OptionSetDTO findById(Long id);

  OptionSetDTO detailById(Long id);

  OptionSetDTO detailByCode(String code);

  List<OptionSetValueDTO> listByCode(String code);

  List<OptionSetDTO> findAll();

  List<OptionSetDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);
}
