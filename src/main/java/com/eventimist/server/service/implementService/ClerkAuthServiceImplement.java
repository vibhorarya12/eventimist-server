package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.clerkResponseDTO.ClerkSessionResponseDTO;
import com.eventimist.server.exceptions.ClerkAuthSessionException;
import com.eventimist.server.service.ClerkAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ClerkAuthServiceImplement implements ClerkAuthService {

    @Value("${clerk.auth.key}")
    private String clerkToken;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean AuthenticateClerkSession(String clerkSessionId) {

        try {
            // Construct the URL dynamically using the clerkSessionId
            String url = "https://api.clerk.com/v1/sessions/" + clerkSessionId;

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clerkToken); // Add Bearer token
            HttpEntity<String> entity = new HttpEntity<>(headers); // Create HTTP entity

            // Make the GET request
            ResponseEntity<ClerkSessionResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ClerkSessionResponseDTO.class
            );

            // Check if response is valid and status is 'active'
            if (response.getStatusCode() == HttpStatus.OK &&
                    "active".equalsIgnoreCase(response.getBody().getStatus())) {
                return true; // Valid session
            }

            // Handle non-active session explicitly
            throw new ClerkAuthSessionException("Session is not active");

        } catch (HttpClientErrorException e) {
            // Handle specific HTTP errors
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ClerkAuthSessionException("Session not found");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ClerkAuthSessionException("Unauthorized request. Invalid API key or token.");
            }
            throw new ClerkAuthSessionException("HTTP error occurred: " + e.getMessage());

        } catch (Exception e) {
            // Handle any other unexpected exceptions
            throw new ClerkAuthSessionException("An unexpected error occurred: " + e.getMessage());
        }
    }
}
