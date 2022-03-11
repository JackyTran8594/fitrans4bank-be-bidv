package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.RoleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface RoleMapRepository extends JpaRepository<RoleMap, Long> {
    @Query(value = "SELECT r.role_list_code FROM role_map r WHERE r.role_id = :roleId", nativeQuery = true)
    List<String> getRoleMap(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_map WHERE role_id = :roleId", nativeQuery = true)
    Integer deleteRoleMap(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_map WHERE role_id IN :roleId", nativeQuery = true)
    Integer deleteRoleMap(@Param("roleId") List<Long> roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_map WHERE role_id = :roleId AND role_list_code IN :roleListCode", nativeQuery = true)
    Integer deleteRoleMap(@Param("roleId") Long roleId, @Param("roleListCode") List<String> roleListCode);


    @Query(value = "SELECT * FROM role_map r WHERE r.role_id = :roleId", nativeQuery = true)
    List<RoleMap> findByRoleId(@Param("roleId") Long roleId);

}
