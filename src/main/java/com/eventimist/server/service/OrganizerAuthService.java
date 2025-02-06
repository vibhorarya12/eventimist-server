package com.eventimist.server.service;

import com.eventimist.server.dto.organizerDTO.*;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrganizerAuthService {
    OrganizerRegisterResponseDTO registerOrganizer (OrganizerRegisterDTO organizerRegisterDTO);
    OrganizerLoginResponseDTO organizerLogin(OrganizerLoginDTO organizerLoginDTO);
    OrganizerRegisterResponseDTO registerWithOauth (OrganizerOauthRegisterDTO oauthRegisterDTO);
    OrganizerLoginResponseDTO loginWithOauth (OauthLoginRequestDTO oauthLoginRequestDTO);
    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);

}
