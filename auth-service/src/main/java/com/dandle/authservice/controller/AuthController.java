package com.dandle.authservice.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dandle.authservice.dto.UserDto;
import com.dandle.authservice.model.Role;
import com.dandle.authservice.model.User;
import com.dandle.authservice.repository.RoleRepository;
import com.dandle.authservice.repository.UserRepository;
import com.dandle.authservice.security.JwtTokenUtil;
import com.dandle.authservice.service.UserService;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        UserRepository userRepository;
        if (userRepository.existsByEmail(userDto.getEmail())) { // made chnage
            return ResponseEntity.badRequest().body(new MessageResponseDto("Error: Email is already in use!"));
        }

        User user = new User(userDto.getEmail(), encoder.encode(userDto.getPassword()));
        
        RoleRepository roleRepository;
        // set roles based on user type
        if (userDto.getUserType().equalsIgnoreCase("staff")) {
            Role staffRole = roleRepository.findByName(RoleName.ROLE_STAFF)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(Collections.singleton(staffRole));
        } else {
            Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(Collections.singleton(customerRole));
        }

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponseDto("User registered successfully!"));
    }


    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
        // Authenticate user credentials
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        // Return JWT token as response
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = getUsernameFromToken(token);
            redisTemplate.delete(username);
            return ResponseEntity.ok("User logged out successfully");
        }
        return ResponseEntity.badRequest().body("Authorization header missing or invalid");
    }

    private String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(getSecretKey()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private byte[] getSecretKey() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        // Extract JWT token from Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String authToken = authHeader.substring(7);

        // Extract user details from JWT token
        final String username = jwtTokenUtil.getUsernameFromToken(authToken);
        final User user = userService.findByUsername(username);

        // Return user details as response
        return ResponseEntity.ok(user);
    }
}
