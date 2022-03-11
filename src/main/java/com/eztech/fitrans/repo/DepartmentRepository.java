package com.eztech.fitrans.repo;

import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>,DepartmentRepositoryCustom {

    @Query(value = "SELECT * FROM department d WHERE d.code = :code ", nativeQuery = true)
    Department findByCode(@Param("code") String code);

    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM department WHERE id IN :ids", nativeQuery = true)
    Integer delete(@Param("ids") List<Long> id);

}
