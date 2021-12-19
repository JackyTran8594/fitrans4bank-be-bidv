package com.eztech.fitrans.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.util.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repo;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = repo.findByUsername(username);
		if (user != null) {
			List<String> role = new ArrayList<>();
			List<Integer> listRole = new ArrayList<>();
			Set<Role> roles = user.getRoles();
			if(DataUtils.notNullOrEmpty(roles)){
				listRole = roles.stream()
						.map(Role::getRoleId)
						.collect(Collectors.toList());
				role = repo.getRoleDetail(listRole);
			}
			return new User(user.getUsername(), user.getPassword(), buildSimpleGrantedAuthorities(roles, role));
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

	private static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles,List<String> roleList) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}

		if(DataUtils.notNullOrEmpty(roleList)){
			for (String role : roleList) {
				authorities.add(new SimpleGrantedAuthority(role));
			}
		}

		return authorities;
	}
}