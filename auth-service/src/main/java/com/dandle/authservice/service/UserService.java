package com.dandle.authservice.service;

import com.dandle.authservice.dto.UserDto;
import com.dandle.authservice.model.Role;
import com.dandle.authservice.model.User;
import com.dandle.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(UserDto userDto) {
        User newUser = new User(
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getFirstName(),
                userDto.getLastName()
        );

        return userRepository.save(newUser);
    }

    private Set<Role> getRolesForNewUser() {
        Set<Role> roles = new HashSet<>();
        Role roleUser = new Role();
        roleUser.setAuthName("ROLE_USER");
        roles.add(roleUser);
        return roles;
    }

    public UserDetails loadUserByUsername(String username) {
        return null;
    }

    public User findByUsername(String username) {
        return null;
    }

}
