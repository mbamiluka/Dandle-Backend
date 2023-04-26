package com.dandle.authservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private List<String> roles;
    public Object getEmail() {
        return null;
    }
    public String getUserType() {
        return null;
    }
}
