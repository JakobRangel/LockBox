package com.lockbox.backend.security;

import com.nimbusds.jose.JOSEException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;

/**
 * Custom filter for extracting and validating JWT from cookies for each HTTP request.
 */
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtCookieAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Attempts to extract a JWT token from cookies, validate it, and authenticate the user for the duration of the request.
     *
     * @param request  the servlet request
     * @param response the servlet response
     * @param chain    the filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String jwt = extractJwtFromCookie(request);

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = jwtTokenUtil.getUserDetails(jwt);
                if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                    authenticateUser(request, userDetails);
                }
            } catch (JOSEException e) {
                throw new ServletException("Error processing JWT", e);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request cookies.
     *
     * @param request the HTTP request to extract cookies from.
     * @return the JWT token, if present; otherwise, null.
     */
    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * Authenticate the user based on the provided user details and set the authentication in the security context.
     *
     * @param request      the servlet request.
     * @param userDetails  the user details retrieved from the token.
     */
    private void authenticateUser(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}