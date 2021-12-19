package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
