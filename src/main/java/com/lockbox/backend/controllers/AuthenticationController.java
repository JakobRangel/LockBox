package com.lockbox.backend.controllers;

import com.lockbox.backend.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.lockbox.backend.security.TokenService;
import com.lockbox.backend.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    UserRepository userRepository;
    public AuthenticationController(AuthenticationManager
                                            authenticationManager,
                                    TokenService tokenService,
                                    UserRepository
                                            userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }
    @PostMapping("/register")
    public void register(@RequestBody User user) {
        try {
            BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
            String passwordEncoded = bc.encode(user.getPassword());
            user.setPassword(passwordEncoded);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/test-token")
    @PreAuthorize("isAuthenticated()") // Ensures the endpoint can only be accessed by authenticated users
    public String testToken(Authentication authentication) {
        return "Access granted to " + authentication.getName() + " with authorities: " + authentication.getAuthorities();
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate token
            String token = tokenService.generateToken(authentication);

            // Setting the cookie
            ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false) // Set to true if you are using HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // Adjust based on your token expiry
                    .sameSite("Lax") // Can use "Strict" for stricter CSRF protection
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // Prepare response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("customerId", user.getId());

            return ResponseEntity.ok(responseData);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    }

