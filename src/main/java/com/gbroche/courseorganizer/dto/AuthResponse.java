package com.gbroche.courseorganizer.dto;

import com.gbroche.courseorganizer.model.User;

public class AuthResponse {
    private UserDTO user;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(User user, String token) {
        this.user = new UserDTO(user);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = new UserDTO(user);
    }
}
