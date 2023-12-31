package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.CustomerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CustomerService {

  CustomerDTO save(CustomerDTO product);

  void deleteById(Long id);

  void deleteById(List<Long> ids);

  CustomerDTO findById(Long id);

  List<CustomerDTO> findAll();

  List<CustomerDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);

  List<CustomerDTO> findByCif(String cif);

  List<CustomerDTO> importFile(MultipartFile file) throws Exception;
}
