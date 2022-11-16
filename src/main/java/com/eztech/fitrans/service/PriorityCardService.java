package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.PriorityCardDTO;
import com.eztech.fitrans.model.PriorityCard;

import java.util.List;
import java.util.Map;

public interface PriorityCardService {

  PriorityCardDTO save(PriorityCardDTO product);

  void deleteById(Long id);

  void deleteById(List<Long> ids);

  PriorityCardDTO findById(Long id);

  List<PriorityCardDTO> findAll();

  List<PriorityCardDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);

}
