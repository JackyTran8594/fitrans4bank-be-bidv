package com.eztech.fitrans.service;

import java.util.List;
import java.util.Map;

import com.eztech.fitrans.dto.response.TransactionTypeDTO;

public interface TransactionTypeService {

    TransactionTypeDTO save(TransactionTypeDTO TransactionType);

    void deleteById(Long id);

    TransactionTypeDTO findById(Long id);

    List<TransactionTypeDTO> findAll();

    List<TransactionTypeDTO> search(Map<String, Object> mapParam);

    Long count(Map<String, Object> mapParam);

    
}
