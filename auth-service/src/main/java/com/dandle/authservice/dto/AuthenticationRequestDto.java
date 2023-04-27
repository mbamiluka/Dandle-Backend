package com.dandle.authservice.dto;

import lombok.AllArgsConstructor; 
import lombok.NoArgsConstructor; 
import lombok.Setter; import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDto {
    private String username;
    private String password;
    public Object getEmail() {
        return null;
    }
}
