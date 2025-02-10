package com.eventimist.server.dto.userDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserOauthLoginDTO {
    @NotBlank(message = "Email must be provided")
    private String email;

    @NotBlank(message = "Clerk session ID must be provided")
    private String clerkSessionId;
}
