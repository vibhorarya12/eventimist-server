package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.userDTO.*;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.*;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.ClerkAuthService;
import com.eventimist.server.service.UserAuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserAuthServiceImplement implements UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    public UserAuthServiceImplement(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private ClerkAuthService clerkAuthService;


    @Override
    public UserRegisterResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        if(checkEmailExists(userRegisterDTO.getEmail())){
            throw new ExistingEntityException("User with this email already exists");
        }

        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        UserEntity user = mapDtoToEntity(userRegisterDTO);
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        user.setPassword(encodedPassword);
       Long userId = userRepository.save(user).getId();
        userRegisterResponseDTO.setEmail(userRegisterDTO.getEmail());
        userRegisterResponseDTO.setName(userRegisterDTO.getName());
        userRegisterResponseDTO.setToken(jwtUtil.generateToken(userRegisterDTO.getEmail(), userId));

        return  userRegisterResponseDTO;
    }

    @Override
    public UserLoginResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userLoginDTO.getEmail());

        if(userOptional.isPresent()){
            if(passwordEncoder.matches(userLoginDTO.getPassword(), userOptional.get().getPassword())){
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
                UserEntity userEntity = userOptional.get();
                userLoginResponseDTO.setName(userEntity.getName());
                userLoginResponseDTO.setEmail(userEntity.getEmail());
                userLoginResponseDTO.setToken(jwtUtil.generateToken(userEntity.getEmail(), userEntity.getId()));
                return  userLoginResponseDTO;
            }
            else {
                throw new WrongCredentialsException("Invalid valid credentials");
            }
        }
    else {
            throw  new EntityNotFoundException("User not found");
        }
    }

    @Override
    public UserLoginResponseDTO loginWithOauth(UserOauthLoginDTO userOauthLoginDTO){
            if(!clerkAuthService.AuthenticateClerkSession(userOauthLoginDTO.getClerkSessionId(), userOauthLoginDTO.getEmail())){
                throw new ClerkAuthSessionException("Clerk session authentication failed || session is inactive");
            }
            Optional<UserEntity> UserOptional = userRepository.findByEmail(userOauthLoginDTO.getEmail());
            if(UserOptional.isPresent()){
                UserEntity userEntity = UserOptional.get();
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
                userLoginResponseDTO.setToken(jwtUtil.generateToken(userEntity.getEmail(), userEntity.getId()));
                userLoginResponseDTO.setEmail(userEntity.getEmail());
                userLoginResponseDTO.setName(userEntity.getName());
                userLoginResponseDTO.setProfilePic(userEntity.getProfilePic());

                return userLoginResponseDTO;

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
}

