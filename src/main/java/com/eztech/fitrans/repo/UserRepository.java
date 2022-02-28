package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

  UserEntity findByUsername(String userName);

  @Query(value = "SELECT r.code FROM role_list r,role_map m WHERE r.id=m.role_list_id AND r.status = 'ACTIVE' AND m.role_id IN :listRole", nativeQuery = true)
  List<String> getRoleDetail(@Param("listRole") List<Long> listRole);

  @Query(value = "SELECT * FROM user_entity u WHERE u.department_id = :departmentId", nativeQuery = true)
  List<UserEntity> findByDepartmentid(@Param("departmentId") Long departmentId);

  @Query(value = "SELECT d.code FROM user_entity u, department d WHERE u.department_id = d.id AND u.username = :username", nativeQuery = true)
  String findCodeByUsername(@Param("username") String username);

}