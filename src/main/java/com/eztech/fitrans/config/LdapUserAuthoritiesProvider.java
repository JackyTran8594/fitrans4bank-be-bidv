package com.eztech.fitrans.config;

import java.util.ArrayList;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import com.eztech.fitrans.repo.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LdapUserAuthoritiesProvider implements AuthenticationProvider {

    private Environment environment;

    @Autowired
    private UserRepository repository;

    // @Value("${spring.ldap.authen.url}")
    private String ldapUrl;

    // @Value("${spring.ldap.authen.dn-patterns}")
    private String dnPatterns;

    // @Value("${spring.ldap.authen.password}")
    private String passwordAttribute;

    // @Value("${spring.ldap.authen.managerDn:#{null}}")
    // @Value("${spring.ldap.authen.managerDn}")
    private String managerDn;

    // @Value("${spring.ldap.authen.managerPassword:#{null}}")
    // @Value("${spring.ldap.authen.managerPassword}")
    private String managerPassword;

    // @Value("${spring.ldap.authen.filter:#{null}}")
    // @Value("${spring.ldap.authen.filter}")
    private String filter;

    public String username;

    public String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    private LdapContextSource contextSource;
    private LdapTemplate ldapTemplate;

    private void initContext() {
        contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(dnPatterns);
        contextSource.setUserDn(managerDn);
        contextSource.setPassword(managerPassword);
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(contextSource);
    }

    public LdapUserAuthoritiesProvider(Environment environment, String ldapUrl, String dnPatterns, String managerDn,
            String managerPassword, String filter) {
        this.environment = environment;
        this.ldapUrl = ldapUrl;
        this.dnPatterns = dnPatterns;
        this.managerDn = managerDn;
        this.managerPassword = managerPassword;
        // this.username = username;
        // this.password = password;
        this.filter = filter;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        initContext();
        Filter filterLdap = new EqualsFilter(filter, authentication.getPrincipal().toString());
        // User user = ldapTemplate.findOne(query().base(dnPatterns).where(filter.encode()), User.class);
        // User user = ldapTemplate.findOne(query()))

        Boolean authenticate = ldapTemplate.authenticate(LdapUtils.newLdapName(dnPatterns), filterLdap.encode(),
                authentication.getCredentials().toString());
        if (authenticate) {
            UserDetails userDetails = new User(authentication.getName(), authentication.getCredentials().toString(),
                    new ArrayList<>());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
                    authentication.getCredentials().toString(), new ArrayList<>());
            return auth;
        } else {

        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
