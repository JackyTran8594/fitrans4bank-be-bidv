package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.CustomerDTO;
import java.util.List;
import java.util.Map;

public interface CustomerService {

  CustomerDTO save(CustomerDTO product);

  void deleteById(Long id);

  CustomerDTO findById(Long id);

  List<CustomerDTO> findAll();

  List<CustomerDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);
}
