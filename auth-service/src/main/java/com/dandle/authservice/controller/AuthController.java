package com.dandle.authservice.controller;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dandle.authservice.dto.JwtAuthenticationResponse;
import com.dandle.authservice.dto.MessageResponseDto;
import com.dandle.authservice.dto.UserDto;
import com.dandle.authservice.model.JwtAuthenticationRequest;
import com.dandle.authservice.model.Role;
import com.dandle.authservice.model.RoleName;
import com.dandle.authservice.model.User;
import com.dandle.authservice.repository.RoleRepository;
import com.dandle.authservice.repository.UserRepository;
import com.dandle.authservice.security.JwtTokenUtil;
import com.dandle.authservice.service.UserService;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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

    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private RedisTemplate<String, Long> redisTemplate;

    //@Value("${DANDLE_SECRET_KEY}")
    private static final String SECRET_KEY=System.getenv("DANDLE_SECRET_KEY");
    private static final int KEY_LENGTH = 256;

    private static final String ALGORITHM = "HmacSHA256 ";


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) { // made chnage
            return ResponseEntity.badRequest().body(new MessageResponseDto("Error: Email is already in use!"));
        }

        Set<Role> roles;
        User user = new User(userDto.getEmail(), encoder.encode(userDto.getPassword()), userDto.getFirstName(), userDto.getLastName());


        
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
            String username = createToken(token);
            redisTemplate.delete(username);
            return ResponseEntity.ok("User logged out successfully");
        }
        return ResponseEntity.badRequest().body("Authorization header missing or invalid");
    }

    public String createToken(String username) {
        long EXPIRATION_TIME=99L * 365L * 24L * 60L * 60L * 1000L;;
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME));
    
        return builder.compact();
    }
    
    private byte[] getSigningKey() {
        return SECRET_KEY.getBytes();
    }
    

    public static SecretKey getSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = new SecureRandom(SECRET_KEY.getBytes());
            keyGen.init(KEY_LENGTH, random);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key", e);
        }
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
