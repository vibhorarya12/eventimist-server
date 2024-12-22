package com.eventimist.server.service;

import com.eventimist.server.dto.organizerDTO.OrganizerLoginDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerLoginResponseDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizerAuthService {
    OrganizerRegisterResponseDTO registerOrganizer (OrganizerRegisterDTO organizerRegisterDTO);
    OrganizerLoginResponseDTO organizerLogin(OrganizerLoginDTO organizerLoginDTO);
    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);
}
