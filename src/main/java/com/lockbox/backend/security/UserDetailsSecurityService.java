package com.lockbox.backend.security;
import com.lockbox.backend.models.User;
import com.lockbox.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User customer = userRepository.findByEmail(email);
            if (customer == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(customer.getEmail())
                    .password(customer.getPassword())  // Will return salted/encrypted password
                    .roles("USER") // Base user role will look into later for adding "admin" roles/functionality
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}

