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

import com.eztech.fitrans.model.Role;

@Slf4j
@RequiredArgsConstructor
@Component
public class LdapUserAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private final UserDetailsService userDetailsService;

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData,
			String username) {
		if (username.equals("admin")) {
			return Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ADMIN));
		} else {
			return userDetailsService.loadUserByUsername(username).getAuthorities();
		}

	}
}