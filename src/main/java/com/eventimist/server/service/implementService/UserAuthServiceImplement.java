package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.userDTO.UserLoginDTO;
import com.eventimist.server.dto.userDTO.UserLoginResponseDTO;
import com.eventimist.server.dto.userDTO.UserRegisterDTO;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.UserAuthService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserUserAuthServiceImplement implements UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUserAuthServiceImplement(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        UserEntity user = mapDtoToEntity(userRegisterDTO);
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Override
    public UserLoginResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userLoginDTO.getEmail());
        UserLoginResponseDTO response = new UserLoginResponseDTO();

        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();

            response.setName(userEntity.getName());
            response.setEmail(userEntity.getEmail());
            response.setAuthenticated(passwordEncoder.matches(userLoginDTO.getPassword(), userEntity.getPassword()));
             return response;
        }
        
        return response;
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

