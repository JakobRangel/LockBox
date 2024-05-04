package com.lockbox.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "lockbox", name="users")
public final class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    private String ipAddress;
    private String country;
    private String role; // Added field for user role
    private String accountStatus; // Added field for account status
    private LocalDateTime lastLogin; // Added field for last login timestamp

    public User() {
    }

    public User(String email, String password, String ipAddress, String country, String role, String accountStatus, LocalDateTime lastLogin) {
        this.email = email;
        this.password = password;
        this.ipAddress = ipAddress;
        this.country = country;
        this.role = role;
        this.accountStatus = accountStatus;
        this.lastLogin = lastLogin;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
