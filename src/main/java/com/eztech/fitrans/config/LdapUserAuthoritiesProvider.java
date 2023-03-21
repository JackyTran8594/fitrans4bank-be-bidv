package com.eztech.fitrans.config;

import java.util.ArrayList;

import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import com.eztech.fitrans.dto.request.LoginRequest;
import com.eztech.fitrans.dto.shareData.ShareDataComponent;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.util.DataUtils;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LdapUserAuthoritiesProvider implements AuthenticationProvider {
    private Logger logger = LoggerFactory.getLogger(LdapUserAuthoritiesProvider.class);

    private Environment environment;

    private UserDetailsService userDetailsService;

    private String ldapUrl;
    private String base;

    private String managerDn;

    private String managerPassword;

    private String filter;

    public String username;

    public String password;

    public Boolean isLdap;

    public void setIsLdap(Boolean isLdap) {
        this.isLdap = isLdap;
    }

    public Boolean getIsLdap() {
        return this.isLdap;
    }

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


    // @Autowired
    // public ShareDataComponent shareDataComponent;

    private LdapContextSource contextSource;
    private LdapTemplate ldapTemplate;

    private void initContext() {
        contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setUserDn(managerDn);
        contextSource.setPassword(managerPassword);
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(contextSource);

    }

    public LdapUserAuthoritiesProvider(Environment environment, String ldapUrl, String dnPatterns, String managerDn,
            String managerPassword, String filter, UserDetailsService userDetailsService) {
        this.environment = environment;
        this.ldapUrl = ldapUrl;
        this.managerDn = managerDn;
        this.managerPassword = managerPassword;
        this.filter = filter;
        this.base = dnPatterns;
        this.userDetailsService = userDetailsService;
    }

    

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        initContext();
        Filter filterLdap = new EqualsFilter(filter, authentication.getPrincipal().toString());
        SearchScope searchScope = SearchScope.SUBTREE;
        LdapQuery query = LdapQueryBuilder.query().base(base).searchScope(searchScope).filter(filterLdap);

        // try {
        ldapTemplate.authenticate(query, authentication.getCredentials().toString());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
                authentication.getCredentials().toString(), new ArrayList<>());
        return auth;
        // } catch (Exception e) {
        // // TODO: handle exception
        // logger.error(e.getMessage(), e);
        // return null;
        // }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
