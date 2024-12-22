package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerDTO.OrganizerLoginDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerLoginResponseDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterResponseDTO;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.CustomException;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.exceptions.ExistingEntityException;
import com.eventimist.server.exceptions.WrongCredentialsException;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.service.CloudinaryService;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class OrganizerAuthServiceImplement implements OrganizerAuthService {
                                
    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CloudinaryService cloudinaryService;


    public OrganizerAuthServiceImplement(OrganizerRepository organizerRepository, PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OrganizerLoginResponseDTO  organizerLogin(OrganizerLoginDTO organizerLoginDTO){
        Optional<OrganizerEntity> organizerOptional =  organizerRepository.findByEmail(organizerLoginDTO.getEmail());

        if(organizerOptional.isPresent()){
            OrganizerEntity organizer = organizerOptional.get();
            if (passwordEncoder.matches(organizerLoginDTO.getPassword(), organizer.getPassword())) {
                OrganizerLoginResponseDTO organizerLoginResponseDTO = new OrganizerLoginResponseDTO();
                organizerLoginResponseDTO.setEmail(organizer.getEmail());
                organizerLoginResponseDTO.setName(organizer.getName());
                organizerLoginResponseDTO.setToken(jwtUtil.generateToken(organizerLoginDTO.getEmail()));
                organizerLoginResponseDTO.setBio(organizer.getBio());
                organizerLoginResponseDTO.setProfilePic(organizer.getProfile_pic());
                return organizerLoginResponseDTO;
            } else {

                throw new WrongCredentialsException("Invalid credentials");
            }


        }
        else {
            throw  new EntityNotFoundException("organizer not found");
        }

    }


    @Override
    public OrganizerRegisterResponseDTO registerOrganizer(OrganizerRegisterDTO organizerRegisterDTO) {
        // Check if the organizer already exists (optional)
        if (checkEmailExists(organizerRegisterDTO.getEmail())) {
            throw new ExistingEntityException("organizer with this email already exists");
        }

        // Map DTO to entity and save
        OrganizerEntity organizerEntity = mapDtoToEntity(organizerRegisterDTO);
        organizerRepository.save(organizerEntity);
        OrganizerRegisterResponseDTO organizerRegisterResponseDTO = new OrganizerRegisterResponseDTO();
        organizerRegisterResponseDTO.setName(organizerRegisterDTO.getName());
        organizerRegisterResponseDTO.setToken(jwtUtil.generateToken(organizerRegisterDTO.getEmail()));
        organizerRegisterResponseDTO.setEmail(organizerRegisterDTO.getEmail());
        return  organizerRegisterResponseDTO;


    }

    private OrganizerEntity mapDtoToEntity(OrganizerRegisterDTO organizerRegisterDTO ) {
        OrganizerEntity organizerEntity = new OrganizerEntity();

        organizerEntity.setName(organizerRegisterDTO.getName());
        organizerEntity.setEmail(organizerRegisterDTO.getEmail());

        // Encode password before saving
        organizerEntity.setPassword(passwordEncoder.encode(organizerRegisterDTO.getPassword()));
        return organizerEntity;
    }
    @Override
    public boolean checkEmailExists(String email) {
        return organizerRepository.findByEmail(email).isPresent();
    }


    @Override
    public UserDetails loadByEmail(String email) throws UsernameNotFoundException {
        Optional<OrganizerEntity> userOptional = organizerRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        OrganizerEntity user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
