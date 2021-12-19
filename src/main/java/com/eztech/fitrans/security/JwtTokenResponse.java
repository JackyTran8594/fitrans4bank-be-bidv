package com.eztech.fitrans.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class JwtTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private long expiresIn = 3600000;
}
