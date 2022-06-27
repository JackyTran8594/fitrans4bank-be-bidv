package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.dto.request.LoginRequest;
import com.eztech.fitrans.dto.request.ValidateTokenRequest;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.security.ApiResponse;
import com.eztech.fitrans.security.JwtAuthenticationResponse;
import com.eztech.fitrans.security.JwtTokenProvider;
import com.eztech.fitrans.service.UserDetailsServiceImpl;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.MessageConstants;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.eztech.fitrans.config.Profiles;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Profile({ Profiles.LDAP_AUTH, Profiles.JWT_AUTH, Profiles.IN_MEMORY_AUTHENTICATION })
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Value("${app.admin.user}")
	private String superAdmin;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername().isEmpty() || loginRequest.getPassword().isEmpty()) {
            return new ResponseEntity(new ApiResponse(false, MessageConstants.USERNAME_OR_PASSWORD_EMPTY),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            UserDetails userDetails = null;
            String jwt = null;
            String departmentCode = null;
            Map<String, Object> mapper = null;
            List<String> permissions = new ArrayList<>();
            String role = null;
            Long userId = null;
            // if (loginRequest.getIsLdap()) {
            if (loginRequest.getIsLdap()) {
                userDetailsServiceImpl.setIsLdap(true);
            } else {
                userDetailsServiceImpl.setIsLdap(false);

            }
            if(loginRequest.getUsername().equals(superAdmin)) {
                userDetailsServiceImpl.setIsAdmin(true);
            } else {
                userDetailsServiceImpl.setIsAdmin(false);
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // TODO: Test
            // log.info(new BCryptPasswordEncoder().encode("123456a@"));
            // log.info("admin: " + new BCryptPasswordEncoder().encode("admin"));

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof UserDetails) {
                    userDetails = (UserDetails) principal;
                    if (userDetails.getUsername().equals(superAdmin)) {
                        role = "ADMIN";
                        jwt = tokenProvider.generateToken(userDetails.getUsername(), role);
                    } else {
                        role = userDetailsServiceImpl.getRoleByUsername(userDetails.getUsername());
                        mapper = userDetailsServiceImpl.getPositionByUsername(userDetails.getUsername());
                        departmentCode = userDetailsServiceImpl
                                .getDepartmentCodeByUsername(userDetails.getUsername());
                        log.info("===SecurityContextHolder getPrincipal UserDetails: " + userDetails.getUsername());
                        if (DataUtils.notNullOrEmpty(userDetails.getAuthorities())) {
                            permissions = userDetails.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList());

                        }
                        jwt = tokenProvider.generateToken(authentication, role, permissions, departmentCode,
                                mapper.get("position").toString(), mapper.get("fullname").toString());
                    }

                } else {
                    log.info("===SecurityContextHolder getPrincipal: "
                            + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                }
            }
            // } else {

            // }

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userDetails));
        } catch (BadCredentialsException ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity(new ApiResponse(false, MessageConstants.USERNAME_OR_PASSWORD_INVALID),
                    HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity(new ApiResponse(false, MessageConstants.USERNAME_INACTIVE),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity(new ApiResponse(false, MessageConstants.SYSTEM_ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = { "/sign-out" }, method = { RequestMethod.POST })
    @ApiOperation(value = "Revoke access token and refresh token", response = ResponseEntity.class)
    public ResponseEntity<?> revokeAccessToken(@RequestBody(required = false) Map<String, String> aTokenMap)
            throws BusinessException {
        try {
            if (aTokenMap != null) {
                if (aTokenMap.containsKey("access_token")) {
                }

                if (aTokenMap.containsKey("refresh_token")) {
                }
            }
            return ResponseEntity.ok("Success");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity(new ApiResponse(false, MessageConstants.SYSTEM_ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/validatetoken")
    public ResponseEntity<?> getTokenByCredentials(@Valid @RequestBody ValidateTokenRequest validateToken) {
        String username = null;
        String jwt = validateToken.getToken();
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            username = tokenProvider.getUsernameFromJWT(jwt);
            // If required we can have one more check here to load the user from LDAP server
            return ResponseEntity.ok(new ApiResponse(Boolean.TRUE, MessageConstants.VALID_TOKEN + username));
        } else {
            return new ResponseEntity(new ApiResponse(false, MessageConstants.INVALID_TOKEN),
                    HttpStatus.BAD_REQUEST);
        }
    }

}