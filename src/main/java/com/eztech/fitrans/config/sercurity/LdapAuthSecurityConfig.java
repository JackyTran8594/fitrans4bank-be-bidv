package com.eztech.fitrans.config.sercurity;

import com.eztech.fitrans.config.JwtAuthenticationEntryPoint;
import com.eztech.fitrans.filter.JwtRequestFilter;
import com.eztech.fitrans.config.LdapUserAuthoritiesPopulator;
import com.eztech.fitrans.config.LdapUserAuthoritiesProvider;
import com.eztech.fitrans.config.Profiles;
import com.eztech.fitrans.model.Role;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Profile({ Profiles.LDAP_AUTH, Profiles.JWT_AUTH, Profiles.IN_MEMORY_AUTHENTICATION })
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class LdapAuthSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String[] AUTH_WHITELIST = {
			// -- swagger ui
			"/swagger-resources/**",
			"/swagger-ui.html",
			"/v2/api-docs",
			"/webjars/**",
			// -- h2 database console
			"/h2-console/**",
			"/*/**",
	};

	@Value("${spring.ldap.authen.url:#{null}}")
	private String ldapUrl;

	@Value("${spring.ldap.domain:#{null}}")
	private String ldapDomain;

	@Value("${spring.ldap.authen.dn-patterns:#{null}}")
	private String dnPatterns;

	// @Value("${spring.ldap.authen.password}")
	// private String passwordAttribute;

	@Value("${spring.ldap.authen.base:#{null}}")
	private String baseDn;

	@Value("${spring.ldap.authen.managerDn:#{null}}")
	private String managerDn;

	@Value("${spring.ldap.authen.managerPassword:#{null}}")
	private String managerPassword;

	@Value("${spring.ldap.authen.filter:#{null}}")
	private String filter;

	@Value("${spring.ldap.authen.groupSearchFilter:#{null}}")
	private String groupSearchFilter;

	@Value("${spring.ldap.authen.groupSearchBase:#{null}}")
	private String groupSearchBase;

	@Value("${app.admin.user}")
	private String superAdmin;

	@Value("${app.admin.password}")
	private String adminPassword;

	private final LdapUserAuthoritiesPopulator ldapUserAuthoritiesPopulator;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final UserDetailsService userDetailsService;
	private final JwtRequestFilter jwtRequestFilter;
	private Environment env;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return rawPassword.toString();
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return encodedPassword.equalsIgnoreCase(rawPassword.toString());
			}

		};
	}

	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		auth.inMemoryAuthentication().withUser(superAdmin).password(encoder.encode(adminPassword))
				.roles(Role.ADMIN);


		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

		auth.authenticationProvider(
		new LdapUserAuthoritiesProvider(env, ldapUrl, baseDn, managerDn,
		managerPassword, filter, userDetailsService))
		.eraseCredentials(false);
		
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http = http.cors().and().csrf().disable();

		http.headers().frameOptions().sameOrigin();
		// Set session management to stateless
		http = http
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and();

		// Set unauthorized requests exception handler
		http = http
				.exceptionHandling()
				.authenticationEntryPoint(
						(request, response, ex) -> {
							response.sendError(
									HttpServletResponse.SC_UNAUTHORIZED,
									ex.getMessage());
						})
				.and();

		// Set permissions on endpoints
		http.authorizeRequests()
				// Our public endpoints
				.antMatchers("/socket/**").permitAll()
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/api/public/**").permitAll()
				.antMatchers("/api/roles/tree").permitAll()
				.antMatchers("/api/customers/test").permitAll()
				.antMatchers("/api/test/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/author/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/author/search").permitAll()
				.antMatchers(HttpMethod.GET, "/api/book/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/book/search").permitAll()
				// Our private endpoints
				.antMatchers("/products/**").hasRole(Role.ADMIN)
				.antMatchers("/api/admin/user/**").hasRole(Role.ADMIN)
				.antMatchers("/api/author/**").hasRole(Role.ADMIN)
				.antMatchers("/api/book/**").hasRole(Role.ADMIN)
				.anyRequest().authenticated();

		// Add JWT token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}
	

	// Add JWT
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}