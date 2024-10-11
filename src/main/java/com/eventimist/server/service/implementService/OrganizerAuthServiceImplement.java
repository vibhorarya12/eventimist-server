package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.service.OrganizerAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OrganizerAuthServiceImplement implements OrganizerAuthService {
                                
    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizerAuthServiceImplement(OrganizerRepository organizerRepository, PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerOrganizer(OrganizerRegisterDTO organizerRegisterDTO) {
        // Check if the organizer already exists (optional)
        if (checkEmailExists(organizerRegisterDTO.getEmail())) {
            throw new IllegalArgumentException("Organizer with email " + organizerRegisterDTO.getEmail() + " already exists");
        }

        // Map DTO to entity and save
        OrganizerEntity organizerEntity = mapDtoToEntity(organizerRegisterDTO);
        organizerRepository.save(organizerEntity);
    }

    private OrganizerEntity mapDtoToEntity(OrganizerRegisterDTO organizerRegisterDTO) {
        OrganizerEntity organizerEntity = new OrganizerEntity();

        organizerEntity.setName(organizerRegisterDTO.getName());
        organizerEntity.setEmail(organizerRegisterDTO.getEmail());

        // Encode password before saving
        organizerEntity.setPassword(passwordEncoder.encode(organizerRegisterDTO.getPassword()));

        organizerEntity.setBio(organizerRegisterDTO.getBio());
        organizerEntity.setProfile_pic(organizerRegisterDTO.getProfilePic());

        return organizerEntity;
    }
    @Override
    public boolean checkEmailExists(String email) {
        return organizerRepository.findByEmail(email).isPresent();
    }
}
