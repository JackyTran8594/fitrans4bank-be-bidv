package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>,DepartmentRepositoryCustom {

    DepartmentDTO findByCode(String code);

}
