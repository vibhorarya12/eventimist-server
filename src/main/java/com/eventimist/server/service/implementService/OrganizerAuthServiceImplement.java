package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerDTO.*;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.OrganizerRefreshTokenEntity;
import com.eventimist.server.entities.OrganizerSubscriptionEntity;
import com.eventimist.server.exceptions.*;
import com.eventimist.server.repository.OrganizerRefreshTokenRepository;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.repository.OrganizerSubscriptionRepository;
import com.eventimist.server.service.ClerkAuthService;
import com.eventimist.server.service.CloudinaryService;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import com.eventimist.server.utils.PasswordGenerator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

@Service
public class OrganizerAuthServiceImplement implements OrganizerAuthService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizerSubscriptionRepository organizerSubscriptionRepository;
    private final OrganizerRefreshTokenRepository organizerRefreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;
    private final ClerkAuthService clerkAuthService;
    private final PasswordGenerator passwordGenerator;

    public OrganizerAuthServiceImplement(
            OrganizerRepository organizerRepository,
            OrganizerSubscriptionRepository organizerSubscriptionRepository,
            OrganizerRefreshTokenRepository organizerRefreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            CloudinaryService cloudinaryService,
            ClerkAuthService clerkAuthService,
            PasswordGenerator passwordGenerator
    ) {
        this.organizerRepository = organizerRepository;
        this.organizerSubscriptionRepository = organizerSubscriptionRepository;
        this.organizerRefreshTokenRepository = organizerRefreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.cloudinaryService = cloudinaryService;
        this.clerkAuthService = clerkAuthService;
        this.passwordGenerator = passwordGenerator;
    }


    // =========================================================
    // ORGANIZER LOGIN
    // =========================================================

    @Override
    public OrganizerAuthResponseDTO organizerLogin(
            OrganizerLoginDTO organizerLoginDTO
    ) {

        Optional<OrganizerEntity> organizerOptional =
                organizerRepository.findByEmail(
                        organizerLoginDTO.getEmail()
                );

        if (organizerOptional.isEmpty()) {
            throw new EntityNotFoundException("organizer not found");
        }

        OrganizerEntity organizer = organizerOptional.get();

        if (!passwordEncoder.matches(
                organizerLoginDTO.getPassword(),
                organizer.getPassword()
        )) {
            throw new WrongCredentialsException("Invalid credentials");
        }

        return createAuthResponse(organizer);
    }


    // =========================================================
    // ORGANIZER REGISTER
    // =========================================================

    @Override
    public OrganizerAuthResponseDTO registerOrganizer(
            OrganizerRegisterDTO organizerRegisterDTO
    ) {

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
        OrganizerEntity organizerEntity =
                mapDtoToEntity(organizerRegisterDTO);

        // Save organizer
        OrganizerEntity savedOrganizer =
                organizerRepository.save(organizerEntity);

        // Create FREE subscription
        OrganizerSubscriptionEntity subscription =
                OrganizerSubscriptionEntity.builder()
                        .organizer(savedOrganizer)
                        .build();

        organizerSubscriptionRepository.save(subscription);

        return createAuthResponse(savedOrganizer);
    }


    // =========================================================
    // OAUTH REGISTER
    // =========================================================

    @Override
    @Transactional
    public OrganizerAuthResponseDTO registerWithOauth(
            OrganizerOauthRegisterDTO oauthRegisterDTO
    ) {

        // Authenticate Clerk Session
        boolean isValidSession =
                clerkAuthService.AuthenticateClerkSession(
                        oauthRegisterDTO.getClerkSessionId(),
                        oauthRegisterDTO.getEmail()
                );

        if (!isValidSession) {
            throw new ClerkAuthSessionException(
                    "Clerk session authentication failed || session is inactive."
            );
        }

        // Check Existing Organizer
        if (checkEmailExists(oauthRegisterDTO.getEmail())) {
            throw new ExistingEntityException(
                    "Organizer with this email already exists."
            );
        }

        // Create Organizer
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

        // Create Default FREE Subscription
        createDefaultFreeSubscription(savedOrganizer);

        return createAuthResponse(savedOrganizer);
    }


    // =========================================================
    // OAUTH LOGIN
    // =========================================================

    @Override
    public OrganizerAuthResponseDTO loginWithOauth(
            OauthLoginRequestDTO oauthLoginRequestDTO
    ) {

        if (!clerkAuthService.AuthenticateClerkSession(
                oauthLoginRequestDTO.getClerkSessionId(),
                oauthLoginRequestDTO.getEmail()
        )) {
            throw new ClerkAuthSessionException(
                    "Clerk session authentication failed || session is inactive."
            );
        }

        Optional<OrganizerEntity> organizerOptional =
                organizerRepository.findByEmail(
                        oauthLoginRequestDTO.getEmail()
                );

        if (organizerOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "organizer not found , please register first !!"
            );
        }

        OrganizerEntity organizer = organizerOptional.get();

        return createAuthResponse(organizer);
    }


    // =========================================================
    // COMMON AUTH RESPONSE
    // =========================================================

    private OrganizerAuthResponseDTO createAuthResponse(
            OrganizerEntity organizer
    ) {

        String accessToken =
                jwtUtil.generateAccessToken(
                        organizer.getEmail(),
                        organizer.getId()
                );

        String refreshToken =
                generateRefreshToken(organizer);

        OrganizerAuthResponseDTO response =
                new OrganizerAuthResponseDTO();

        response.setName(organizer.getName());
        response.setEmail(organizer.getEmail());
        response.setBio(organizer.getBio());
        response.setProfilePic(organizer.getProfile_pic());
        response.setCoverImage(organizer.getCover_image());
        response.setLocation(organizer.getLocation());

        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }


    // =========================================================
    // GENERATE REFRESH TOKEN
    // =========================================================

    private String generateRefreshToken(
            OrganizerEntity organizer
    ) {

        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        String refreshToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);

        OrganizerRefreshTokenEntity entity =
                new OrganizerRefreshTokenEntity();

        entity.setTokenHash(hashToken(refreshToken));
        entity.setOrganizer(organizer);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(
                LocalDateTime.now().plusDays(30)
        );
        entity.setRevoked(false);

        organizerRefreshTokenRepository.save(entity);

        return refreshToken;
    }


    // =========================================================
    // HASH REFRESH TOKEN
    // =========================================================

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to hash refresh token",
                    e
            );
        }
    }


    // =========================================================
    // REFRESH ACCESS TOKEN
    // =========================================================

    @Override
    @Transactional
    public OrganizerAuthResponseDTO refreshAccessToken(
            String refreshToken
    ) {

        String tokenHash =
                hashToken(refreshToken);

        OrganizerRefreshTokenEntity tokenEntity =
                organizerRefreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );

        if (tokenEntity.isRevoked()) {
            throw new RuntimeException(
                    "Refresh token has been revoked"
            );
        }

        if (tokenEntity.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        OrganizerEntity organizer =
                tokenEntity.getOrganizer();

        // Generate new access token
        String newAccessToken =
                jwtUtil.generateAccessToken(
                        organizer.getEmail(),
                        organizer.getId()
                );

        // Revoke old refresh token
        tokenEntity.setRevoked(true);
        organizerRefreshTokenRepository.save(tokenEntity);

        // Generate new refresh token
        String newRefreshToken =
                generateRefreshToken(organizer);

        // Build response
        OrganizerAuthResponseDTO response =
                new OrganizerAuthResponseDTO();

        response.setName(organizer.getName());
        response.setEmail(organizer.getEmail());
        response.setBio(organizer.getBio());
        response.setProfilePic(organizer.getProfile_pic());
        response.setCoverImage(organizer.getCover_image());
        response.setLocation(organizer.getLocation());

        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);

        return response;
    }


    // =========================================================
    // REVOKE REFRESH TOKEN / LOGOUT
    // =========================================================

    @Override
    public void revokeRefreshToken(
            String refreshToken
    ) {

        String tokenHash =
                hashToken(refreshToken);

        organizerRefreshTokenRepository
                .findByTokenHash(tokenHash)
                .ifPresent(token -> {

                    token.setRevoked(true);

                    organizerRefreshTokenRepository.save(token);
                });
    }


    // =========================================================
    // CREATE FREE SUBSCRIPTION
    // =========================================================

    private void createDefaultFreeSubscription(
            OrganizerEntity organizer
    ) {

        OrganizerSubscriptionEntity subscription =
                OrganizerSubscriptionEntity.builder()
                        .organizer(organizer)
                        .build();

        organizerSubscriptionRepository.save(subscription);
    }


    // =========================================================
    // CHECK EMAIL
    // =========================================================

    @Override
    public boolean checkEmailExists(String email) {

        return organizerRepository
                .findByEmail(email)
                .isPresent();
    }


    // =========================================================
    // LOAD USER
    // =========================================================

    @Override
    public UserDetails loadByEmail(String email)
            throws UsernameNotFoundException {

        Optional<OrganizerEntity> organizerOptional =
                organizerRepository.findByEmail(email);

        if (organizerOptional.isEmpty()) {

            throw new UsernameNotFoundException(
                    "Organizer not found with email: " + email
            );
        }

        OrganizerEntity organizer =
                organizerOptional.get();

        return new org.springframework.security.core.userdetails.User(
                organizer.getEmail(),
                organizer.getPassword(),
                new ArrayList<>()
        );
    }


    // =========================================================
    // MAP REGISTER DTO
    // =========================================================

    private OrganizerEntity mapDtoToEntity(
            OrganizerRegisterDTO organizerRegisterDTO
    ) {

        OrganizerEntity organizerEntity =
                new OrganizerEntity();

        organizerEntity.setName(
                organizerRegisterDTO.getName()
        );

        organizerEntity.setEmail(
                organizerRegisterDTO.getEmail()
        );

        organizerEntity.setPassword(
                passwordEncoder.encode(
                        organizerRegisterDTO.getPassword()
                )
        );

        return organizerEntity;
    }
}