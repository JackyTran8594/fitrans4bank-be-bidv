package com.eztech.fitrans.config.sercurity;

import com.eztech.fitrans.config.JwtAuthenticationEntryPoint;
import com.eztech.fitrans.filter.JwtRequestFilter;
import com.eztech.fitrans.config.LdapUserAuthoritiesPopulator;
import com.eztech.fitrans.config.Profiles;
import com.eztech.fitrans.model.Role;
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

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Profile(Profiles.LDAP_AUTH)
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

	@Value("${spring.ldap.authen.url}")
	private String ldapUrl;

	@Value("${spring.ldap.authen.dn-patterns}")
	private String dnPatterns;

	@Value("${spring.ldap.authen.password}")
	private String passwordAttribute;

	@Value("${spring.ldap.authen.managerDn:#{null}}")
	private String managerDn;

	@Value("${spring.ldap.authen.managerPassword:#{null}}")
	private String managerPassword;

	private final LdapUserAuthoritiesPopulator ldapUserAuthoritiesPopulator;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final UserDetailsService jwtUserDetailsService;
	private final JwtRequestFilter jwtRequestFilter;

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

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		auth.inMemoryAuthentication().withUser("adminbidv").password(passwordEncoder().encode("admin@123")).roles(Role.ADMIN);

		auth
				.ldapAuthentication()
				.userDnPatterns(dnPatterns)
//				.groupSearchBase("ou=groups")
				.contextSource()
				.url(ldapUrl)
				.managerDn(managerDn)
				.managerPassword(managerPassword)
				.and()
				.passwordCompare()
				.passwordEncoder(new BCryptPasswordEncoder())
//				.passwordEncoder(passwordEncoder())
				.passwordAttribute(passwordAttribute).and()
				// Populates the user roles by LDAP user name from database
				.ldapAuthoritiesPopulator(ldapUserAuthoritiesPopulator);
		;



//		// Returns LdapAuthenticationProviderConfigurer to allow customization of the
//		// LDAP authentication
//		auth.ldapAuthentication()
//				// Pass the LDAP patterns for finding the username.
//				// The key "{0}" will be substituted with the username
//				.userDnPatterns("uid={0},ou=users")
//				// Pass search base as argument for group membership searches.
//				.groupSearchBase("ou=groups")
//				// Configures base LDAP path context source
//				.contextSource().url("ldap://localhost:10389/dc=javachinna,dc=com")
//				// DN of the user who will bind to the LDAP server to perform the search
//				.managerDn("uid=admin,ou=system")
//				// Password of the user who will bind to the LDAP server to perform the search
//				.managerPassword("secret").and()
//				// Configures LDAP compare operation of the user password to authenticate
//				.passwordCompare().passwordEncoder(new LdapShaPasswordEncoder())
//				// Specifies the attribute in the directory which contains the user password.
//				// Defaults to "userPassword".
//				.passwordAttribute("userPassword").and()
//				// Populates the user roles by LDAP user name from database
//				.ldapAuthoritiesPopulator(ldapUserAuthoritiesPopulator);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http = http.cors().and().csrf().disable();

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
									ex.getMessage()
							);
						}
				)
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
		http.addFilterBefore(jwtRequestFilter,UsernamePasswordAuthenticationFilter.class);
	}

	//Backup
//	@Override
//	protected void configure(HttpSecurity httpSecurity) throws Exception {
//		// Disable CSRF
//		httpSecurity.csrf().disable()
//				// Only admin can perform HTTP delete operation
//				.authorizeRequests().antMatchers(HttpMethod.DELETE).hasRole(Role.ADMIN)
//				// any authenticated user can perform all other operations
//				.antMatchers("/products/**").hasAnyRole(Role.ADMIN, Role.USER)
//				// Permit all other request without authentication
//				.and().authorizeRequests().anyRequest().permitAll()
//				// Reject every unauthenticated request and send error code 401.
//				.and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//				// We don't need sessions to be created.
//				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//		// Add a filter to validate the tokens with every request
//		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//	}

	//Add JWT
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}