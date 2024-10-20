package com.eventimist.server.security;

import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.service.UserAuthService;
import com.eventimist.server.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserAuthService userAuthService;
    private final OrganizerAuthService organizerAuthService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserAuthService userAuthService , OrganizerAuthService organizerAuthService) {
        this.jwtUtil = jwtUtil;
        this.userAuthService = userAuthService;
        this.organizerAuthService = organizerAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtil.extractEmail(jwt);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            // Check if the request is for an organizer endpoint
            if (request.getRequestURI().startsWith("/api/organizer/")) {
                // Try loading organizer details
                try {
                    userDetails = this.organizerAuthService.loadByEmail(email);
                } catch (EntityNotFoundException ex) {

                    // Organizer not found, userDetails remains null

                }
            } else {
                // Try loading user details from UserAuthService
                try {
                    userDetails = this.userAuthService.loadByEmail(email);
                } catch (UsernameNotFoundException ex) {
                    // User not found, userDetails remains null
                }
            }

            // If the correct userDetails are found, validate the token
            if (userDetails != null && jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

}
