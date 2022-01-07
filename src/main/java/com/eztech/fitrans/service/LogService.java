package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.ActionLogDTO;

import java.util.List;
import java.util.Map;

public interface LogService {

    void deleteById(Long id);

    List<ActionLogDTO> search(Map<String, Object> mapParam);

    Long count(Map<String, Object> mapParam);

}
