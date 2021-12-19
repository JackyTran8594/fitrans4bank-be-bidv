package com.eztech.fitrans.security;

import com.eztech.fitrans.util.DataUtils;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private boolean success = false;
    private JwtTokenResponse token;
    private String email;
    private UserDetails user;
    private List<String> role;

    public JwtAuthenticationResponse(String accessToken,UserDetails userDetails) {
        this.accessToken = accessToken;
        JwtTokenResponse tokenResponse = new JwtTokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setExpiresIn(3600000);
        this.token = tokenResponse;
        this.success = true;
        this.user = userDetails;
        if(userDetails != null) {
            this.email = userDetails.getUsername();
            if(DataUtils.notNullOrEmpty(userDetails.getAuthorities())){
                role = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
            }
        }
    }
}
