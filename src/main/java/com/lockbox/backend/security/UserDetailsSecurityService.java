package com.lockbox.backend.security;
import com.lockbox.backend.models.User;
import com.lockbox.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Custom UserDetailsService that integrates with our UserRepository to load user details.
 * This service is used by Spring Security to perform user authentication.
 */
@Component
public class UserDetailsSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Constructor for UserDetailsSecurityService, which is used to inject the UserRepository.
     * @param userRepository The repository used for user data access.
     */
    @Autowired
    public UserDetailsSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by their email. It is used by Spring Security during the authentication process.
     *
     * @param email The email through which to search the user.
     * @return UserDetails object that Spring Security uses for validating and holding authenticated user information.
     * @throws UsernameNotFoundException If the user is not found or there is an issue in retrieving user information.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Constructing UserDetails object with user's data
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())  // Encrypted password as stored in the database
                .roles("USER")  // Default role, expand this part for managing multiple roles
                .build();
    }
}

