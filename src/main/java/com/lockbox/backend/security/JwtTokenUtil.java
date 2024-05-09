package com.lockbox.backend.security;

import com.nimbusds.jose.JOSEException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;
import com.nimbusds.jose.jwk.RSAKey;


/**
 * Utility class for handling operations related to JSON Web Tokens (JWT).
 */
@Component
public class JwtTokenUtil {

    private final RSAKey rsaKey;

    @Autowired
    public JwtTokenUtil(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Retrieves user details from a JWT.
     *
     * @param token the JWT to parse.
     * @return UserDetails constructed from the JWT claims.
     * @throws JOSEException if there is an error processing the JWT.
     */
    public UserDetails getUserDetails(String token) throws JOSEException {
        String username = getUsernameFromToken(token);
        String authorities = getClaimFromToken(token, claims -> claims.get("authorities", String.class));

        if (authorities != null) {
            return new User(username, "",
                    Arrays.stream(authorities.split(","))
                            .map(SimpleGrantedAuthority::new)
                            .toList());
        } else {
            return new User(username, "", new ArrayList<>());
        }
    }

    /**
     * Extracts the username from a JWT.
     *
     * @param token the JWT to parse.
     * @return the username from the JWT.
     * @throws JOSEException if there is an error processing the JWT.
     */
    public String getUsernameFromToken(String token) throws JOSEException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Resolves a specific claim from the JWT.
     *
     * @param token the JWT to parse.
     * @param claimsResolver the function to apply to the claims.
     * @return the result of applying the claims resolver.
     * @throws JOSEException if there is an error processing the JWT.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws JOSEException {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates a JWT against the user's details.
     *
     * @param token the JWT to validate.
     * @param userDetails the user details to validate against.
     * @return true if the token is valid, false otherwise.
     * @throws JOSEException if there is an error processing the JWT.
     */
    public boolean validateToken(String token, UserDetails userDetails) throws JOSEException {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the JWT has expired.
     *
     * @param token the JWT to check.
     * @return true if the token has expired, false otherwise.
     * @throws JOSEException if there is an error processing the JWT.
     */
    private Boolean isTokenExpired(String token) throws JOSEException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT.
     *
     * @param token the JWT to parse.
     * @return the expiration date of the token.
     * @throws JOSEException if there is an error processing the JWT.
     */
    public Date getExpirationDateFromToken(String token) throws JOSEException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a JWT.
     *
     * @param token the JWT to parse.
     * @return the claims contained in the JWT.
     * @throws JOSEException if there is an error processing the JWT.
     */
    public Claims getAllClaimsFromToken(String token) throws JOSEException {
        return Jwts.parserBuilder()
                .setSigningKey(rsaKey.toRSAPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}