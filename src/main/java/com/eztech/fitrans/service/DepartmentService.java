package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.DepartmentDTO;

import java.util.List;

public interface DepartmentService {
    DepartmentDTO save(DepartmentDTO product);

    void deleteById(Long id);

    DepartmentDTO findById(Long id);

    List<DepartmentDTO> findAll();
}
