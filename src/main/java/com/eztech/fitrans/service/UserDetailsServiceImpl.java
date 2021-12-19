package com.eztech.fitrans.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.UserRepository;
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
			return new User(user.getUsername(), user.getPassword(), buildSimpleGrantedAuthorities(user.getRoles()));
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

	private static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}
}