package com.eventimist.server.controllers;

import com.eventimist.server.dto.common.RefreshTokenRequestDTO;
import com.eventimist.server.dto.organizerDTO.*;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.service.OrganizerAuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/auth/organizer")
public class OrganizerAuthController {

    @Autowired
    private OrganizerAuthService organizerAuthService;


    @PostMapping("/login")
    public ResponseEntity<OrganizerAuthResponseDTO> login(
            @Valid @RequestBody OrganizerLoginDTO organizerLoginDTO
    ) {

        OrganizerAuthResponseDTO response =
                organizerAuthService.organizerLogin(
                        organizerLoginDTO
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<OrganizerAuthResponseDTO> register(
            @Valid @RequestBody OrganizerRegisterDTO organizerRegisterDTO
    ) {

        OrganizerAuthResponseDTO response =
                organizerAuthService.registerOrganizer(
                        organizerRegisterDTO
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/oauthRegister")
    public ResponseEntity<OrganizerAuthResponseDTO> oAUthRegister(
            @Valid @RequestBody OrganizerOauthRegisterDTO oauthRegisterDTO
    ) {

        OrganizerAuthResponseDTO response =
                organizerAuthService.registerWithOauth(
                        oauthRegisterDTO
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/oauthLogin")
    public ResponseEntity<OrganizerAuthResponseDTO> oAUthLogin(
            @Valid @RequestBody OauthLoginRequestDTO oauthLoginRequestDTO
    ) {

        OrganizerAuthResponseDTO response =
                organizerAuthService.loginWithOauth(
                        oauthLoginRequestDTO
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<OrganizerAuthResponseDTO> refreshAccessToken(
            @RequestBody RefreshTokenRequestDTO request
    ) {

        OrganizerAuthResponseDTO response =
                organizerAuthService.refreshAccessToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody RefreshTokenRequestDTO request
    ) {

        organizerAuthService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }


    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(
            @RequestParam(required = false)
            @NotBlank(message = "Email must be provided")
            String email
    ) {

        boolean emailExists =
                organizerAuthService.checkEmailExists(email);

        if (emailExists) {
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Email exists",
                            "exists", true
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Email not found",
                        "exists", false
                )
        );
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {

        throw new EntityNotFoundException("not found bhai");
    }
}

