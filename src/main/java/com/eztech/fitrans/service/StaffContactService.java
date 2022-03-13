package com.eztech.fitrans.service;

import java.util.List;
import java.util.Map;

import com.eztech.fitrans.dto.response.StaffContactDTO;

public interface StaffContactService {

    StaffContactDTO save(StaffContactDTO staffContact);

    void deleteById(Long id);

    StaffContactDTO findById(Long id);

    List<StaffContactDTO> findAll();

    List<StaffContactDTO> search(Map<String, Object> mapParam);

    Long count(Map<String, Object> mapParam);

    Boolean findByCif(Long id, String cif);

    StaffContactDTO findByCustomerId(Long id);
}
