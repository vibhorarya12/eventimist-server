package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerDTO.*;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.*;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.service.ClerkAuthService;
import com.eventimist.server.service.CloudinaryService;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import com.eventimist.server.utils.PasswordGenerator;
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

    @Autowired
    private ClerkAuthService clerkAuthService;

    @Autowired
    private  PasswordGenerator passwordGenerator;


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
                organizerLoginResponseDTO.setToken(jwtUtil.generateToken(organizer.getEmail(), organizer.getId() ));
                organizerLoginResponseDTO.setBio(organizer.getBio());
                organizerLoginResponseDTO.setProfilePic(organizer.getProfile_pic());
                organizerLoginResponseDTO.setCoverImage(organizer.getCover_image());
                organizerLoginResponseDTO.setLocation(organizer.getLocation());
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

        // Authenticate Clerk session
        if (!clerkAuthService.AuthenticateClerkSession(organizerRegisterDTO.getClerkSessionId(),organizerRegisterDTO.getEmail())) {
            throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive.");
        }

        // Check if the organizer already exists
        if (checkEmailExists(organizerRegisterDTO.getEmail())) {
            throw new ExistingEntityException("Organizer with this email already exists.");
        }

        // Map DTO to entity
        OrganizerEntity organizerEntity = mapDtoToEntity(organizerRegisterDTO);

        // Save organizer to repository
      Long userId =   organizerRepository.save(organizerEntity).getId();

        // Generate response DTO
        OrganizerRegisterResponseDTO organizerRegisterResponseDTO = new OrganizerRegisterResponseDTO();
        organizerRegisterResponseDTO.setName(organizerRegisterDTO.getName());
        organizerRegisterResponseDTO.setEmail(organizerRegisterDTO.getEmail());
        organizerRegisterResponseDTO.setToken(jwtUtil.generateToken(organizerRegisterDTO.getEmail() , userId));

        // Return response DTO
        return organizerRegisterResponseDTO;
    }


    @Override
    public OrganizerRegisterResponseDTO registerWithOauth (OrganizerOauthRegisterDTO oauthRegisterDTO){
        if (!clerkAuthService.AuthenticateClerkSession(oauthRegisterDTO.getClerkSessionId(),oauthRegisterDTO.getEmail())) {
            throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive.");
        }
        // Check if the organizer already exists
        if (checkEmailExists(oauthRegisterDTO.getEmail())) {
            throw new ExistingEntityException("Organizer with this email already exists.");
        }
        OrganizerEntity organizer = new OrganizerEntity();
        organizer.setName(oauthRegisterDTO.getName());
        organizer.setEmail(oauthRegisterDTO.getEmail());
        organizer.setProfile_pic(oauthRegisterDTO.getProfilePic());
       organizer.setPassword(passwordEncoder.encode(passwordGenerator.generateStrongPassword(12)));

        Long userId =   organizerRepository.save(organizer).getId();

        OrganizerRegisterResponseDTO organizerRegisterResponseDTO = new OrganizerRegisterResponseDTO();
        organizerRegisterResponseDTO.setName(organizer.getName());
        organizerRegisterResponseDTO.setEmail(organizer.getEmail());
        organizerRegisterResponseDTO.setProfilePic(organizer.getProfile_pic());
        organizerRegisterResponseDTO.setToken(jwtUtil.generateToken(organizer.getEmail(), userId));

        return organizerRegisterResponseDTO;



    }


    @Override
    public OrganizerLoginResponseDTO loginWithOauth (OauthLoginRequestDTO oauthLoginRequestDTO){
        if (!clerkAuthService.AuthenticateClerkSession(oauthLoginRequestDTO.getClerkSessionId(), oauthLoginRequestDTO.getEmail())) {
            throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive.");
        }
        Optional<OrganizerEntity> organizerOptional =  organizerRepository.findByEmail(oauthLoginRequestDTO.getEmail());
        if(organizerOptional.isPresent()){
            OrganizerEntity organizer = organizerOptional.get();
            OrganizerLoginResponseDTO organizerLoginResponseDTO = new OrganizerLoginResponseDTO();
            organizerLoginResponseDTO.setEmail(organizer.getEmail());
            organizerLoginResponseDTO.setName(organizer.getName());
            organizerLoginResponseDTO.setToken(jwtUtil.generateToken(organizer.getEmail(), organizer.getId() ));
            organizerLoginResponseDTO.setBio(organizer.getBio());
            organizerLoginResponseDTO.setProfilePic(organizer.getProfile_pic());
            organizerLoginResponseDTO.setCoverImage(organizer.getCover_image());
            organizerLoginResponseDTO.setLocation(organizer.getLocation());
            return organizerLoginResponseDTO;

        }
        else {
            throw  new EntityNotFoundException("organizer not found , please register first !!");
        }


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

    private OrganizerEntity mapDtoToEntity(OrganizerRegisterDTO organizerRegisterDTO ) {
        OrganizerEntity organizerEntity = new OrganizerEntity();

        organizerEntity.setName(organizerRegisterDTO.getName());
        organizerEntity.setEmail(organizerRegisterDTO.getEmail());

        // Encode password before saving
        organizerEntity.setPassword(passwordEncoder.encode(organizerRegisterDTO.getPassword()));
        return organizerEntity;
    }
}
