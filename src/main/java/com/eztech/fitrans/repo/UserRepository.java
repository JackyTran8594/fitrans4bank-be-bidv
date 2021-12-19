package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByUsername(String userName);
}
