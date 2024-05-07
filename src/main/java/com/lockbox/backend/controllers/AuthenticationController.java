package com.lockbox.backend.controllers;

import com.lockbox.backend.models.User;
import jakarta.servlet.http.Cookie;
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

import java.time.LocalDateTime;
import java.util.Collections;
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
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
            String passwordEncoded = bc.encode(user.getPassword());
            user.setPassword(passwordEncoded);
            user.setAccountStatus("ACTIVE");
            user.setLastLogin(LocalDateTime.now());

            // Check if the email already exists
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                return ResponseEntity.badRequest().body("Email already exists!");
            }
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("User successfully created");
    }
    @GetMapping("/auth-status")
    public ResponseEntity<?> checkAuthStatus(Authentication authentication) {
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
        return ResponseEntity.ok(Collections.singletonMap("isLoggedIn", isLoggedIn));
    }
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Delete token cookie
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        // Delete userId cookie
        Cookie userIdCookie = new Cookie("userId", null);
        userIdCookie.setMaxAge(0);
        userIdCookie.setPath("/");
        response.addCookie(userIdCookie);
        return ResponseEntity.ok("Signed out successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User authenticatedUser = userRepository.findByEmail(user.getEmail());
            if(authenticatedUser.getAccountStatus().equalsIgnoreCase("DISABLED")) {
                return ResponseEntity.badRequest().body("Account Disabled.");
            }

            // Generate token
            String token = tokenService.generateToken(authentication);

            // Setting the cookie
            ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // Set to true if you are using HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // Adjust based on your token expiry
                    .sameSite("None") // Can use "Strict" for stricter CSRF protection
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // Setting the userId cookie
            ResponseCookie userIdCookie = ResponseCookie.from("userId", String.valueOf(authenticatedUser.getId()))
                    .httpOnly(true)
                    .secure(true) // Set to true if you are using HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // Adjust based on your token expiry
                    .sameSite("None") // Can use "Strict" for stricter CSRF protection
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, userIdCookie.toString());
            authenticatedUser.setLastLogin(LocalDateTime.now());
            return ResponseEntity.ok("User successfully authenticated");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}

