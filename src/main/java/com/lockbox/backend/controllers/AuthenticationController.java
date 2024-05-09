package com.lockbox.backend.controllers;

import com.lockbox.backend.models.User;
import com.lockbox.backend.repositories.GeolocationRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

import static com.lockbox.backend.repositories.GeolocationRepository.getClientIp;
import static com.lockbox.backend.repositories.GeolocationRepository.getCountryByIp;



/**
 * Handles authentication-related actions including registration, login, logout,
 * and checking authentication status.
 */
@RestController
@RequestMapping("/auth") // Centralized base path for better organization
@CrossOrigin // Consider specifying origins if you know the client locations
public class AuthenticationController {

    @Value("${app.token.duration.hours:1}")
    private long tokenValidityDuration;

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    TokenService tokenService,
                                    UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Registers a new user account in the system.
     *
     * @param user the user details to register.
     * @param request the HTTP request to extract IP and other details.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user, HttpServletRequest request) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIpAddress(request.getRemoteAddr());
        user.setCountry(GeolocationRepository.getCountryByIp(request.getRemoteAddr()));
        user.setRole("USER");
        user.setAccountStatus("ACTIVE");
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok("User successfully created");
    }

    /**
     * Checks if the user is authenticated.
     *
     * @param authentication current authentication object to verify status.
     * @return ResponseEntity with the login status.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> checkAuthStatus(Authentication authentication) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        return ResponseEntity.ok(Collections.singletonMap("isLoggedIn", isLoggedIn));
    }

    /**
     * Logs out the current user by clearing the relevant cookies.
     *
     * @param response the HTTP response to modify cookies.
     * @return ResponseEntity indicating the outcome of the logout operation.
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        clearCookie(response, "token");
        clearCookie(response, "userId");
        return ResponseEntity.ok("Signed out successfully");
    }

    /**
     * Attempts to authenticate the user with provided credentials.
     *
     * @param user the user attempting to log in.
     * @param response the HTTP response to set cookies upon successful authentication.
     * @return ResponseEntity with the outcome of the authentication attempt.
     */

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User authenticatedUser = userRepository.findByEmail(user.getEmail());
            if ("DISABLED".equalsIgnoreCase(authenticatedUser.getAccountStatus())) {
                return ResponseEntity.badRequest().body("Account Disabled.");
            }

            setupCookies(response, authenticatedUser, authentication);

            authenticatedUser.setLastLogin(LocalDateTime.now());
            userRepository.save(authenticatedUser);

            return ResponseEntity.ok("User successfully authenticated");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    // Utility method to clear cookies
    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // Utility method to set up authentication and user cookies
    private void setupCookies(HttpServletResponse response, User user, Authentication authentication) {
        String token = tokenService.generateToken(authentication);
        addCookie(response, "token", token, tokenValidityDuration);
        addCookie(response, "userId", String.valueOf(user.getId()), tokenValidityDuration);
    }

    private void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}