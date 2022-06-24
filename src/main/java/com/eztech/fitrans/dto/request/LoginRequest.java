package com.eztech.fitrans.dto.request;

public class LoginRequest {
    private String username;

    private String password;

    private Boolean isLdap;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsLdap(Boolean isLdap) {
        this.isLdap = isLdap;
    }

    public Boolean getIsLdap() {
        return this.isLdap;
    }
}