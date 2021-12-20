package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.security.JwtTokenProvider;
import com.eztech.fitrans.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl jwtUserDetailsService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @GetMapping("/current")
    public ResponseEntity<?> current() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = "";
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
                log.info("===UserController SecurityContextHolder getPrincipal UserDetails: " + username);
            } else {
                username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                log.info("===UserController SecurityContextHolder getPrincipal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            }

            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            return ResponseEntity.ok(userDetails);
        }
        return ResponseEntity.ok(null);
    }
}