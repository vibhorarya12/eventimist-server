package com.eventimist.server.service;

import com.eventimist.server.dto.organizerDTO.*;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrganizerAuthService {

    OrganizerAuthResponseDTO registerOrganizer(
            OrganizerRegisterDTO organizerRegisterDTO
    );

    OrganizerAuthResponseDTO organizerLogin(
            OrganizerLoginDTO organizerLoginDTO
    );

    OrganizerAuthResponseDTO registerWithOauth(
            OrganizerOauthRegisterDTO oauthRegisterDTO
    );

    OrganizerAuthResponseDTO loginWithOauth(
            OauthLoginRequestDTO oauthLoginRequestDTO
    );

    UserDetails loadByEmail(String email);

    boolean checkEmailExists(String email);

    OrganizerAuthResponseDTO refreshAccessToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);
}