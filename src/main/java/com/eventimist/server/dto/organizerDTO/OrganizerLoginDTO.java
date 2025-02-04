package com.eventimist.server.dto.organizerDTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizerLoginDTO {
    @NotBlank(message = "Email is required.")
    private String email;
    @NotBlank(message = "Password is required.")
    private String password;
}
