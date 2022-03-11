package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>,DepartmentRepositoryCustom {

    @Query(value = "SELECT * FROM department d WHERE d.code = :code ", nativeQuery = true)
    Department findByCode(@Param("code") String code);

}
