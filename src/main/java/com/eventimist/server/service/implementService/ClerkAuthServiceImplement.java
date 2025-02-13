package com.eventimist.server.service.implementService;

import com.eventimist.server.exceptions.ClerkAuthSessionException;
import com.eventimist.server.service.ClerkAuthService;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class ClerkAuthServiceImplement implements ClerkAuthService {

    @Value("${clerk.auth.key}")
    private String clerkToken;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON Parser

    @Override
    public boolean AuthenticateClerkSession(String clerkSessionId, String email) {
        try {
            // Step 1: Fetch session details from Clerk API
            String sessionUrl = "https://api.clerk.com/v1/sessions/" + clerkSessionId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clerkToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> sessionResponse = restTemplate.exchange(
                    sessionUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (sessionResponse.getStatusCode() != HttpStatus.OK) {
                throw new ClerkAuthSessionException("Invalid session response");
            }

            // Parse JSON response to get user ID
            JsonNode sessionJson = objectMapper.readTree(sessionResponse.getBody());
            if (!"active".equalsIgnoreCase(sessionJson.path("status").asText())) {
                throw new ClerkAuthSessionException("Session is not active");
            }

            String userId = sessionJson.path("user_id").asText();
            if (userId.isEmpty()) {
                throw new ClerkAuthSessionException("User ID not found in session");
            }

            // Step 2: Fetch user details from Clerk API
            return validateUserEmail(userId, email);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ClerkAuthSessionException("Session not found");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ClerkAuthSessionException("Unauthorized request. Invalid API key.");
            }
            throw new ClerkAuthSessionException("HTTP error: " + e.getMessage());
        } catch (Exception e) {
            throw new ClerkAuthSessionException("Unexpected error: " + e.getMessage());
        }
    }



    @Override
    public boolean deleteExistingClerkUser (String email){
        Optional<String> userIdOptional = retrieveClerkUser(email);

        if (userIdOptional.isEmpty()) {
//            System.out.println("no user found in clerk db <<<<<<<<<<<<<<<");
            return false;
        }

//        System.out.println("Proceeding to delete <<<<<<<<<<<<<<<");
        String userId = userIdOptional.get();
        String deleteUserUrl = "https://api.clerk.dev/v1/users/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(clerkToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                deleteUserUrl,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
        return response.getStatusCode() == HttpStatus.NO_CONTENT;

    }





// helpers ///

    public Optional<String> retrieveClerkUser(String email) {
        System.out.println("Retreiving clerk user frm email <<<<<<<<<<<<<<<");
        String getUserUrl = "https://api.clerk.dev/v1/users?email_address=" + email;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(clerkToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getUserUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ClerkAuthSessionException("Clerk user authentication failed");
        }

        JSONArray usersArray = new JSONArray(response.getBody());
        if (usersArray.length()==0) {
            return Optional.empty();
        }

        JSONObject user = usersArray.getJSONObject(0);
        return Optional.of(user.getString("id"));
    }




    private boolean validateUserEmail(String userId, String email) {
        try {
            String userUrl = "https://api.clerk.com/v1/users/" + userId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(clerkToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> userResponse = restTemplate.exchange(
                    userUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (userResponse.getStatusCode() != HttpStatus.OK) {
                throw new ClerkAuthSessionException("Failed to fetch user details");
            }

            // Parse JSON response to verify email
            JsonNode userJson = objectMapper.readTree(userResponse.getBody());
            JsonNode emailAddresses = userJson.path("email_addresses");

            for (JsonNode emailNode : emailAddresses) {
                if (email.equalsIgnoreCase(emailNode.path("email_address").asText())) {
                    return true;
                }
            }

            throw new ClerkAuthSessionException("Email mismatch: session does not belong to provided email");

        } catch (Exception e) {
            throw new ClerkAuthSessionException("Error fetching user details: " + e.getMessage());
        }
    }
}
