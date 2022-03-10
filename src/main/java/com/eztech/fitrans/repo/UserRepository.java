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

  @Query(value = "SELECT r.code FROM role_list r,role_map m WHERE r.code=m.role_list_code AND r.status = 'ACTIVE' AND m.role_id IN :listRole AND r.type = 1", nativeQuery = true)
  List<String> getRoleDetail(@Param("listRole") List<Long> listRole);

  @Query(value = "SELECT * FROM user_entity u WHERE u.department_id = :departmentId", nativeQuery = true)
  List<UserEntity> findByDepartmentid(@Param("departmentId") Long departmentId);

  @Query(value = "SELECT d.code FROM user_entity u, department d WHERE u.department_id = d.id AND u.username = :username", nativeQuery = true)
  String findCodeByUsername(@Param("username") String username);

  @Query(value = "SELECT u.* FROM user_entity u, department d WHERE u.department_id = d.id AND d.code = :departmentCode", nativeQuery = true)
  List<UserEntity> findByDepartmentCode(@Param("departmentCode") String departmentCode);

  @Query(value = "SELECT r.name FROM user_entity u, role r, user_role ur WHERE u.id = ur.user_id AND r.id = ur.role_id AND u.username = :username", nativeQuery = true)
  String findRoleByUsername(@Param("username") String username);

  @Query(value = "SELECT r.id FROM  user_role ur WHERE ur.user_id = :userId", nativeQuery = true)
  Long findRoleIdByUserId(@Param("userId") Long userId);

  @Query(value = "UPDATE user_role SET role_id = :roleId  WHERE user_id = :userId", nativeQuery = true)
  Boolean updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

}