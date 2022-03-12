package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

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

  @Query(value = "SELECT TOP  1 ur.role_id FROM  user_role ur WHERE ur.user_id = :userId", nativeQuery = true)
  Long findRoleIdByUserId(@Param("userId") Long userId);

  
  @Modifying
  @Query(value = "UPDATE user_role SET role_id = :roleId  WHERE user_id = :userId", nativeQuery = true)
  Integer updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO user_role (user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
  Integer createUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

  @Modifying
  @Query(value = "DELETE FROM user_role WHERE user_id = :userId AND role_id != :roleId", nativeQuery = true)
  Integer deleteByRoleUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

  @Modifying
  @Transactional
  @Query(value = "DELETE  FROM user_entity WHERE id IN :ids", nativeQuery = true)
  Integer delete(@Param("ids") List<Long> id);

  @Query(value = "SELECT r.name  FROM role r WHERE r.id in (SELECT role_id from user_role ur WHERE ur.user_id = :userId) and r.status = 'ACTIVE'", nativeQuery = true)
  List<String> listRole(@Param("userId") Long userId);

}