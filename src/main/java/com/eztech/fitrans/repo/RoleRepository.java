package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {
    Role findByName(String name);

    @Query(value = "SELECT * FROM role r WHERE r.id IN (SELECT role_id from user_role u where u.user_id = :userId)", nativeQuery = true)
    List<Role> getRole(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM role WHERE id IN :ids", nativeQuery = true)
    Integer delete(@Param("ids") List<Long> id);

}
