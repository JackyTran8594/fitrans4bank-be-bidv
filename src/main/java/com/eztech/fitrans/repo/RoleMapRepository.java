package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.RoleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface RoleMapRepository extends JpaRepository<RoleMap, Long> {
    @Query(value = "SELECT * FROM role_map r WHERE r.role_id = :roleId", nativeQuery = true)
    List<Long> getRoleMap(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_map WHERE role_id = :roleId", nativeQuery = true)
    Integer deleteRoleMap(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_map WHERE role_id = :roleId AND role_list_id IN :roleListId", nativeQuery = true)
    Integer deleteRoleMap(@Param("roleId") Long roleId, @Param("roleListId") List<Long> roleListId);

}
