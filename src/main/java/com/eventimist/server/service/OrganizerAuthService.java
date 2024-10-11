package com.eventimist.server.service;

import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;

public interface OrganizerAuthService {
    void registerOrganizer (OrganizerRegisterDTO organizerRegisterDTO);
    boolean checkEmailExists(String email);
}
