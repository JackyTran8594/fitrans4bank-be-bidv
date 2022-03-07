package com.eztech.fitrans.service;

import com.eztech.fitrans.model.Department;
import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.DepartmentRepository;
import com.eztech.fitrans.repo.RoleRepository;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.util.DataUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@CacheConfig(cacheNames = { "UserDetailsServiceImpl" }, cacheManager = "localCacheManager")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repo;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private DepartmentRepository departmentRepo;

	@Override
	@Cacheable(key = "#username", cacheManager = "localCacheManager")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = repo.findByUsername(username);
		if (user != null) {
			if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
				throw new UsernameNotFoundException("User not found with username: " + username);
			}

			List<String> role = new ArrayList<>();
			List<Long> listRole = new ArrayList<>();
			List<Role> roles = roleRepository.getRole(user.getId());
			if (DataUtils.notNullOrEmpty(roles)) {
				listRole = roles.stream()
						.map(Role::getId)
						.collect(Collectors.toList());
				role = repo.getRoleDetail(listRole);
			}
			return new User(user.getUsername(), user.getPassword(), buildSimpleGrantedAuthorities(roles, role));
		} else {
			log.warn("User not found with username {} ---> Create in db", username);
			user = new UserEntity();
			user.setEmail(username + "@bidv.com.vn");
			user.setFullName(username);
			user.setUsername(username);
			user.setStatus("ACTIVE");
			user.setPassword("");
			repo.save(user);
			return new User(user.getUsername(), user.getPassword(),
					buildSimpleGrantedAuthorities(new ArrayList<>(), new ArrayList<>()));
		}
	}

	private static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final List<Role> roles,
			List<String> roleList) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}

		if (DataUtils.notNullOrEmpty(roleList)) {
			for (String role : roleList) {
				authorities.add(new SimpleGrantedAuthority(role));
			}
		}

		return authorities;
	}

	public String getDepartmentCodeByUsername(String username) {
		// UserEntity user = repo.findByUsername(username);
		String code = repo.findCodeByUsername(username);
		if (code == null) {
			throw new UsernameNotFoundException("Username not found with not found: " + username);
		}
		return code;
	}

	public String getRoleByUsername(String username) {
		String role = repo.findRoleByUsername(username);
		if (role == null) {
			throw new UsernameNotFoundException("Username not found with not found: " + username);

		}
		return role;
	}
}