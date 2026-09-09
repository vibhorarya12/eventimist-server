package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.userDTO.*;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.*;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.ClerkAuthService;
import com.eventimist.server.service.UserAuthService;
import com.eventimist.server.utils.JwtUtil;
import com.eventimist.server.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import com.eventimist.server.entities.UserRefreshTokenEntity;
import com.eventimist.server.repository.UserRefreshTokenRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class UserAuthServiceImplement implements UserAuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    @Autowired
    private JwtUtil jwtUtil;
    public UserAuthServiceImplement(UserRepository userRepository, PasswordEncoder passwordEncoder , UserRefreshTokenRepository userRefreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRefreshTokenRepository =
                userRefreshTokenRepository;
    }

    @Autowired
    private ClerkAuthService clerkAuthService;

    @Autowired
    private PasswordGenerator passwordGenerator;


    @Override
    public UserAuthResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        if(!clerkAuthService.AuthenticateClerkSession(userRegisterDTO.getClerkSessionId(), userRegisterDTO.getEmail())){
            throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive.");
        }
        if(checkEmailExists(userRegisterDTO.getEmail())){
            throw new ExistingEntityException("User with this email already exists");
        }



        UserEntity user = mapDtoToEntity(userRegisterDTO);
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user).getId();
//        userRegisterResponseDTO.setEmail(userRegisterDTO.getEmail());
//        userRegisterResponseDTO.setName(userRegisterDTO.getName());
//        userRegisterResponseDTO.setToken(jwtUtil.generateAccessToken(userRegisterDTO.getEmail(), userId));


        return createAuthResponse(user) ;
    }

    @Override
    public UserAuthResponseDTO loginUser(UserLoginDTO userLoginDTO) {

        Optional<UserEntity> userOptional =
                userRepository.findByEmail(userLoginDTO.getEmail());

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        UserEntity userEntity = userOptional.get();

        if (!passwordEncoder.matches(
                userLoginDTO.getPassword(),
                userEntity.getPassword()
        )) {
            throw new WrongCredentialsException("Invalid valid credentials");
        }

        return createAuthResponse(userEntity);


    }


    @Override
    public  UserAuthResponseDTO registerWithOauth (UserOauthRegisterDTO userOauthRegisterDTO){
        if(!clerkAuthService.AuthenticateClerkSession(userOauthRegisterDTO.getClerkSessionId(), userOauthRegisterDTO.getEmail())){
            throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive");
        }
        if(checkEmailExists(userOauthRegisterDTO.getEmail())){
            throw new ExistingEntityException("User with this email already exists");
        }
        UserEntity user = new UserEntity();
        user.setName(userOauthRegisterDTO.getName());
        user.setEmail(userOauthRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(passwordGenerator.generateStrongPassword(12)));
        user.setProfilePic(userOauthRegisterDTO.getProfilePic());

        userRepository.save(user).getId();

//        response.setToken(jwtUtil.generateAccessToken(userOauthRegisterDTO.getEmail(), userId));
//        response.setName(userOauthRegisterDTO.getName());
//        response.setEmail(userOauthRegisterDTO.getEmail());
//        response.setProfilePic(userOauthRegisterDTO.getProfilePic());

        return  createAuthResponse(user);


    }




    @Override
    public UserAuthResponseDTO loginWithOauth(UserOauthLoginDTO userOauthLoginDTO){
            if(!clerkAuthService.AuthenticateClerkSession(userOauthLoginDTO.getClerkSessionId(), userOauthLoginDTO.getEmail())){
                throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive");
            }
            Optional<UserEntity> UserOptional = userRepository.findByEmail(userOauthLoginDTO.getEmail());
            if(UserOptional.isPresent()){
                UserEntity userEntity = UserOptional.get();
//                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
//                userLoginResponseDTO.setToken(jwtUtil.generateAccessToken(userEntity.getEmail(), userEntity.getId()));
//                userLoginResponseDTO.setEmail(userEntity.getEmail());
//                userLoginResponseDTO.setName(userEntity.getName());
//                userLoginResponseDTO.setProfilePic(userEntity.getProfilePic());

                return createAuthResponse(userEntity);

            }
            else {
                throw  new EntityNotFoundException("User not found");
            }

    }


    private UserEntity mapDtoToEntity(UserRegisterDTO userRegisterDTO) {
        UserEntity user = new UserEntity();
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());

        return user;
    }

    @Override
    public UserDetails loadByEmail(String email) throws UsernameNotFoundException {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        UserEntity user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }


    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }



    @Override
    public UserAuthResponseDTO refreshAccessToken(
            String refreshToken
    ) {

        String tokenHash =
                hashToken(refreshToken);

        UserRefreshTokenEntity tokenEntity =
                userRefreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->

                                new BadRequestException( "Invalid refresh token")

                        );

        if (tokenEntity.isRevoked()) {

            throw new RefreshAccessTokenException("Refresh token has been revoked");
        }

        if (tokenEntity.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RefreshAccessTokenException( "Refresh token has expired");
        }

        UserEntity user =
                tokenEntity.getUser();

        /*
         * Generate new access token
         */
        String newAccessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getId()
                );

        /*
         * Rotate refresh token
         */
        tokenEntity.setRevoked(true);

        userRefreshTokenRepository.save(
                tokenEntity
        );

        String newRefreshToken =
                generateRefreshToken(user);

        UserAuthResponseDTO response =
                new UserAuthResponseDTO();

        response.setName(
                user.getName()
        );

        response.setEmail(
                user.getEmail()
        );

        response.setProfilePic(
                user.getProfilePic()
        );

        response.setToken(
                newAccessToken
        );

        response.setRefreshToken(
                newRefreshToken
        );

        return response;
    }


    @Override
    public void revokeRefreshToken(
            String refreshToken
    ) {

        String tokenHash =
                hashToken(refreshToken);

        userRefreshTokenRepository
                .findByTokenHash(tokenHash)
                .ifPresent(token -> {

                    token.setRevoked(true);

                    userRefreshTokenRepository.save(
                            token
                    );
                });
    }


    private String generateRefreshToken(
            UserEntity user
    ) {

        SecureRandom secureRandom =
                new SecureRandom();

        byte[] randomBytes =
                new byte[64];

        secureRandom.nextBytes(randomBytes);

        String refreshToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);

        UserRefreshTokenEntity entity =
                new UserRefreshTokenEntity();

        entity.setTokenHash(
                hashToken(refreshToken)
        );

        entity.setUser(user);

        entity.setCreatedAt(
                LocalDateTime.now()
        );

        entity.setExpiresAt(
                LocalDateTime.now().plusDays(30)
        );

        entity.setRevoked(false);

        userRefreshTokenRepository.save(entity);

        return refreshToken;
    }

    private String hashToken(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes()
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


   //common response
    private UserAuthResponseDTO createAuthResponse(UserEntity user) {

        String accessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getId()
                );

        String refreshToken =
                generateRefreshToken(user);

        UserAuthResponseDTO response =
                new UserAuthResponseDTO();

        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setProfilePic(user.getProfilePic());
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

}


