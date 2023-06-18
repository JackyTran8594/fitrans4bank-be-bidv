package com.eztech.fitrans.service;

import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.DepartmentRepository;
import com.eztech.fitrans.repo.RoleRepository;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.util.DataUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
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

	public Boolean isLdap;

	public Boolean isAdmin;

	public void setIsLdap(Boolean isLdap) {
		this.isLdap = isLdap;
	}

	public Boolean getIsLdap(Boolean isLdap) {
		return this.isLdap;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsAdmin(Boolean isAdmin) {
		return this.isAdmin;
	}

	@Override
	@Cacheable(key = "#username", cacheManager = "localCacheManager")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = repo.findByUsername(username);
		// if(isLdap) {
		if (user != null) {
			if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
				throw new UsernameNotFoundException("User exist but not active - User not found with username: " + username);
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
			throw new UsernameNotFoundException("User not login LDAP - User not found with username: " + username);
			// if (isLdap) {
			// 	log.warn("User login ldap ok but not found with username in db {} ---> Create in db", username);
			// 	user = new UserEntity();
			// 	user.setEmail(username + "@bidv.com.vn");
			// 	user.setFullName(username);
			// 	user.setUsername(username);
			// 	user.setStatus("ACTIVE");
			// 	user.setPassword("$2a$10$xgMeNxDvGTeI2u/MwPqKV.oIq8O1OeDEhcy8k19V.dTvLpWe88xRS");
			// 	// user.setPosition(PositionTypeEnum.UNKNOWN.getName());
			// 	// user.
			// 	repo.save(user);
			// 	return new User(user.getUsername(), user.getPassword(),
			// 			buildSimpleGrantedAuthorities(new ArrayList<>(), new ArrayList<>()));
			// } else {
			// 	// add lần đầu
			// 	if (isAdmin) {
			// 		user = new UserEntity();
			// 		user.setEmail(username + "@bidv.com.vn");
			// 		user.setFullName(username);
			// 		user.setUsername(username);
			// 		user.setStatus("ACTIVE");
			// 		user.setPassword("$2a$10$xgMeNxDvGTeI2u/MwPqKV.oIq8O1OeDEhcy8k19V.dTvLpWe88xRS");
			// 		// user.setPosition(PositionTypeEnum.UNKNOWN.getName());
			// 		repo.save(user);
			// 		return new User(user.getUsername(), user.getPassword(),
			// 				buildSimpleGrantedAuthorities(new ArrayList<>(), new ArrayList<>()));
			// 	} else {
			// 		throw new UsernameNotFoundException("User not login LDAP - User not found with username: " + username);
			// 	}
			// }

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
			log.warn("User login ldap ok but not found deparment with username in db:", username);
			// throw new UsernameNotFoundException("Username not found department: " + username);
			code = "UNKNOWN";
		}
		return code;
	}

	public String getRoleByUsername(String username) {
		String role = repo.findRoleByUsername(username);
		if (DataUtils.isNullOrEmpty(role)) {
			log.warn("User login ldap ok but not found role with username in db:" + username);
			// throw new UsernameNotFoundException("Username not found role: " + username);
			role = Role.ROLE_USER;
		} 
		return role;
	}

	public Map<String, Object> getPositionByUsername(String username) {
		UserEntity user = repo.findByUsername(username);
		Map<String, Object> mapper = new HashMap<String, Object>();
		String position = null;
		String fullname = null;
		Long departmentId = null;
		if (DataUtils.isNullOrEmpty(user)) {
			log.warn("User login ldap ok but not found with username in db:" + username);
			// throw new UsernameNotFoundException("Username not found position: " + username);
			mapper.put("position", "UNKNOWN");
			mapper.put("fullname", "UNKNOWN");
			mapper.put("departmentId", "UNKNOWN");
		} else {
			// Integer priorityCard = repo.getPriorityCardByDepartmentId(user.getDepartmentId());
			position = !DataUtils.isNullOrEmpty(user.getPosition()) ? user.getPosition() : "UNKNOWN";
			fullname = !DataUtils.isNullOrEmpty(user.getFullName()) ? user.getFullName() : "UNKNOWN";
			departmentId = !DataUtils.isNullOrEmpty(user.getDepartmentId()) ? user.getDepartmentId() : null;
			mapper.put("position", position);
			mapper.put("fullname", fullname);
			mapper.put("departmentId", departmentId);
		}
		return mapper;

	}


}