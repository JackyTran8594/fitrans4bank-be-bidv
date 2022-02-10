package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.RoleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleListRepository extends JpaRepository<RoleList, Long>, RoleRepositoryCustom {
	@Query(value = "SELECT * FROM role_list r WHERE r.code = :code and r.status = 'ACTIVE'", nativeQuery = true)
	RoleList findByCode(@Param("code") String code);
}
