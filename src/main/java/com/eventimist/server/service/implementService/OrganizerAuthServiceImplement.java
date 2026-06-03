package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerDTO.*;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.OrganizerSubscriptionEntity;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.*;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.repository.OrganizerSubscriptionRepository;
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
import org.springframework.transaction.annotation.Transactional;
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

     @Autowired
    private final OrganizerSubscriptionRepository organizerSubscriptionRepository;


    public OrganizerAuthServiceImplement(OrganizerRepository organizerRepository,OrganizerSubscriptionRepository organizerSubscriptionRepository, PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
        this.organizerSubscriptionRepository = organizerSubscriptionRepository;

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
        if (!clerkAuthService.AuthenticateClerkSession(
                organizerRegisterDTO.getClerkSessionId(),
                organizerRegisterDTO.getEmail()
        )) {
            throw new ClerkAuthSessionException(
                    "Clerk session authentication failed || session is inactive."
            );
        }

        // Check if organizer already exists
        if (checkEmailExists(organizerRegisterDTO.getEmail())) {
            throw new ExistingEntityException(
                    "Organizer with this email already exists."
            );
        }

        // Map DTO to entity
        OrganizerEntity organizerEntity = mapDtoToEntity(organizerRegisterDTO);

        // Save organizer
        OrganizerEntity savedOrganizer =
                organizerRepository.save(organizerEntity);

        // Create FREE subscription
        OrganizerSubscriptionEntity subscription =
                OrganizerSubscriptionEntity.builder()
                        .organizer(savedOrganizer)
                        .build();

        organizerSubscriptionRepository.save(subscription);

        // Generate response DTO
        OrganizerRegisterResponseDTO organizerRegisterResponseDTO =
                new OrganizerRegisterResponseDTO();

        organizerRegisterResponseDTO.setName(savedOrganizer.getName());

        organizerRegisterResponseDTO.setEmail(savedOrganizer.getEmail());

        organizerRegisterResponseDTO.setToken(
                jwtUtil.generateToken(
                        savedOrganizer.getEmail(),
                        savedOrganizer.getId()
                )
        );

        return organizerRegisterResponseDTO;
    }


    @Override
    @Transactional
    public OrganizerRegisterResponseDTO registerWithOauth(
            OrganizerOauthRegisterDTO oauthRegisterDTO
    ) {

        // ─── Authenticate Clerk Session ─────────────────────────────────────────
        boolean isValidSession = clerkAuthService.AuthenticateClerkSession(
                oauthRegisterDTO.getClerkSessionId(),
                oauthRegisterDTO.getEmail()
        );

        if (!isValidSession) {
            throw new ClerkAuthSessionException(
                    "Clerk session authentication failed || session is inactive."
            );
        }

        // ─── Check Existing Organizer ───────────────────────────────────────────
        if (checkEmailExists(oauthRegisterDTO.getEmail())) {
            throw new ExistingEntityException(
                    "Organizer with this email already exists."
            );
        }

        // ─── Create Organizer ───────────────────────────────────────────────────
        OrganizerEntity organizer = new OrganizerEntity();

        organizer.setName(oauthRegisterDTO.getName());
        organizer.setEmail(oauthRegisterDTO.getEmail());
        organizer.setProfile_pic(oauthRegisterDTO.getProfilePic());

        organizer.setPassword(
                passwordEncoder.encode(
                        passwordGenerator.generateStrongPassword(12)
                )
        );

        OrganizerEntity savedOrganizer =
                organizerRepository.save(organizer);

        // ─── Create Default FREE Subscription ───────────────────────────────────
        createDefaultFreeSubscription(savedOrganizer);

        // ─── Generate JWT Token ─────────────────────────────────────────────────
        String token = jwtUtil.generateToken(
                savedOrganizer.getEmail(),
                savedOrganizer.getId()
        );

        // ─── Build Response DTO ─────────────────────────────────────────────────
        OrganizerRegisterResponseDTO responseDTO =
                new OrganizerRegisterResponseDTO();

        responseDTO.setName(savedOrganizer.getName());
        responseDTO.setEmail(savedOrganizer.getEmail());
        responseDTO.setProfilePic(savedOrganizer.getProfile_pic());
        responseDTO.setToken(token);

        return responseDTO;
    }

    private void createDefaultFreeSubscription(
            OrganizerEntity organizer
    ) {

        OrganizerSubscriptionEntity subscription =
                OrganizerSubscriptionEntity.builder()
                        .organizer(organizer)
                        .build();

        organizerSubscriptionRepository.save(subscription);
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
