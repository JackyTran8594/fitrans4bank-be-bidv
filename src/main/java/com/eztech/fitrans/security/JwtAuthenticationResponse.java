package com.eztech.fitrans.security;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private boolean success = false;
    private JwtTokenResponse token;
    private String email;
    private UserDetails user;

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
        }
    }
}
