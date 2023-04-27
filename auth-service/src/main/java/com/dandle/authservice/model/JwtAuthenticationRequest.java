package com.dandle.authservice.model;

public class JwtAuthenticationRequest {

    private String username;
    private String password;

    public JwtAuthenticationRequest() {}

    public JwtAuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
