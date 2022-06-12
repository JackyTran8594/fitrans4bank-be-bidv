package com.eztech.fitrans.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.DataUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class LdapUserAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private final UserDetailsService userDetailsService;
	private final UserService userService;

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData,
			String username) {
		// if (username.equals("admin")) {
		// 	return Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ADMIN));
		// } else {	
		// 	return userDetailsService.loadUserByUsername(username).getAuthorities();
		// }
		// Collection<? extends GrantedAuthority> authorities = userDetailsService.loadUserByUsername(username).getAuthorities();
		// if(!DataUtils.isNullOrEmpty(authorities)) {	
		// 	return authorities;
		// } else {
		// 	UserDTO user = userService.findByUsername(username);
		// 	// List<String> roles = 
		// 	if(!DataUtils.isNullOrEmpty(user)) {
		// 		return Arrays.asList(new SimpleGrantedAuthority(username));
		// 	}
		// }
		return userDetailsService.loadUserByUsername(username).getAuthorities();
	}
}