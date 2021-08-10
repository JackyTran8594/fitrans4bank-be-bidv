package com.eztech.fitrans.authentication;

import com.eztech.fitrans.authentication.exception.TokenValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationTokenService {

    private Properties properties;

    @Autowired
    private AuthenticationTokenService(Properties properties) {
        this.properties = properties;
    }

    public AuthenticationToken createToken(String token) throws TokenValidationException {
        return new AuthenticationToken(token, properties.getAccessTokenSecretKey());
    }
}
