package com.eventimist.server.dto.organizerDTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OauthLoginRequestDTO {
    @NotBlank(message = "Email must be provided")
    private String email;

    @NotBlank(message = "Clerk session ID must be provided")
    private String clerkSessionId;

}
