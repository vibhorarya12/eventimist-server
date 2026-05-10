package com.eventimist.server.security;

import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.exceptions.JwtAuthException;
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

        // Skip JWT processing for public endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/") || requestURI.startsWith("/api/public")  ) {
            chain.doFilter(request, response); // Allow request without JWT check
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        Long userId = null;
        String jwt = null;

        try {
            // Check if the Authorization header is present and starts with "Bearer "
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);

                // Extract email from JWT
                email = jwtUtil.extractEmail(jwt);
                userId = jwtUtil.extractUserId(jwt);
                System.out.println("user Id is : " + userId);
            } else {
                throw new JwtAuthException("Authorization header is missing or invalid!");
            }

            // Proceed if email is extracted and no authentication exists in the SecurityContext
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;

                // Determine whether the request is for organizer or user
                if (request.getRequestURI().startsWith("/api/organizer/")) {
                    userDetails = this.organizerAuthService.loadByEmail(email);
                } else {
                    userDetails = this.userAuthService.loadByEmail(email);
                }

                // Validate the JWT token and set authentication
                if (userDetails != null && jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }


                else {

                    throw new JwtAuthException("JWT token validation failed!");
                }
            }
            // Continue the filter chain
            chain.doFilter(request, response);

        }

        catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // Handle expired JWT
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT token is expired!\"}");
        } catch (io.jsonwebtoken.SignatureException ex) {
            // Handle invalid signature
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid JWT signature!\"}");
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            // Handle malformed JWT
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Malformed JWT token!\"}");
        }

        catch (JwtAuthException ex) {
            // Catch and handle custom JWT exceptions
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        } catch (UsernameNotFoundException | EntityNotFoundException ex) {
            // Handle case when user or organizer not found
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User or Organizer not found!\"}");
        } catch (Exception ex) {
            // Handle unexpected errors
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal Server Error: " + ex.getMessage() + "\"}");
        }
    }


}
