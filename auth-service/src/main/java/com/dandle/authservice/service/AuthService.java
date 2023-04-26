package com.dandle.authservice.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dandle.authservice.dto.AuthenticationRequestDto;
import com.dandle.authservice.dto.AuthenticationResponseDto;
import com.dandle.authservice.dto.UserDto;
import com.dandle.authservice.model.Role;
import com.dandle.authservice.model.User;
import com.dandle.authservice.repository.RoleRepository;
import com.dandle.authservice.repository.UserRepository;
import com.dandle.authservice.security.JwtTokenUtil;
import com.dandle.authservice.security.JwtUserDetailsService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    //private UserDetailsService userDetailsService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtUserDetailsService jwtUserDetailsService,
                       JwtTokenUtil jwtTokenUtil,
                       UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getEmail(), authenticationRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtTokenUtil.generateToken(userDetails);
        return new AuthenticationResponseDto(jwtToken);
    }

    public UserDto register(UserDto userDto, RoleName roleName) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }
        User user = new User(userDto.getName(), userDto.getEmail(), userDto.getPassword());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singleton(role));
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getName(), savedUser.getEmail());
    }

    public Boolean validate(String jwtToken) {
        return jwtTokenUtil.validateToken(jwtToken);
    }

    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            jwtTokenUtil.invalidateToken(jwtToken);
        }
    }
}
