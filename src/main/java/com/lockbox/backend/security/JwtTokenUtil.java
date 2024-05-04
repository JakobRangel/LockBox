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


@Component
public class JwtTokenUtil {

    private final RSAKey rsaKey;

    @Autowired
    public JwtTokenUtil(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    public UserDetails getUserDetails(String token) throws JOSEException {
        String username = getUsernameFromToken(token);
        String authorities = getClaimFromToken(token, claims -> claims.get("authorities", String.class));
        if (authorities != null) {
            return new User(username, "", Arrays.asList(authorities.split(",")).stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        } else {
            // If authorities claim is not present, return an empty list of authorities
            return new User(username, "", new ArrayList<>());
        }
    }


    public String getUsernameFromToken(String token) throws JOSEException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws JOSEException {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) throws JOSEException {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) throws JOSEException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) throws JOSEException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Claims getAllClaimsFromToken(String token) throws JOSEException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(rsaKey.toRSAPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}