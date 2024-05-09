package com.lockbox.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing user details in the LockBox application.
 * This class maps to the 'users' table in the 'lockbox' schema of the database.
 */
@Entity
@Table(schema = "lockbox", name = "users")
public final class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String ipAddress;
    private String country;

    @Column(nullable = false)
    private String role; // Field for user role

    @Column(nullable = false)
    private String accountStatus; // Field for account status

    @Column(nullable = false)
    private LocalDateTime lastLogin; // Field for last login timestamp

    /**
     * Default constructor for JPA.
     */
    public User() {
    }

    /**
     * Constructs a new User instance with all fields initialized.
     */
    public User(int id, String email, String password, String ipAddress, String country, String role, String accountStatus, LocalDateTime lastLogin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.ipAddress = ipAddress;
        this.country = country;
        this.role = role;
        this.accountStatus = (accountStatus != null) ? accountStatus : "ACTIVE";
        this.lastLogin = (lastLogin != null) ? lastLogin : LocalDateTime.now();
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
