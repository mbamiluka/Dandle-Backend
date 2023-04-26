package com.dandle.authservice.dto;

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
}
