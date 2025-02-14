package com.eventimist.server.dto.userDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank (message = "name not provided")
    private String name;
    @NotBlank (message = "email not provided")
    private String email;
    @NotBlank (message = "password not provided")
    private String password;
    @NotBlank (message = "please provide a valid clerkSessionId")
    private String clerkSessionId;

}
