package com.eventimist.server.controllers;


import com.eventimist.server.dto.common.RefreshTokenRequestDTO;
import com.eventimist.server.dto.userDTO.*;
import com.eventimist.server.exceptions.ExistingEntityException;
import com.eventimist.server.service.ClerkAuthService;
import com.eventimist.server.service.UserAuthService;
import com.eventimist.server.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/user")
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private  ClerkAuthService clerkAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("register")
    public ResponseEntity<?>register(@Valid @RequestBody UserRegisterDTO userRegisterDTO){

           UserAuthResponseDTO userRegisterResponseDTO = userAuthService.registerUser(userRegisterDTO);

            return  new ResponseEntity<>(userRegisterResponseDTO, HttpStatus.OK);

    }


    @PostMapping("oauthRegister")
    public ResponseEntity<UserAuthResponseDTO> oauthRegister (@Valid @RequestBody UserOauthRegisterDTO userOauthRegisterDTO){
         UserAuthResponseDTO  response = userAuthService.registerWithOauth(userOauthRegisterDTO);
        return new ResponseEntity<UserAuthResponseDTO>(response , HttpStatus.OK);
    }


    @PostMapping("oauthLogin")
    public ResponseEntity<?> oauthLogin (@Valid @RequestBody UserOauthLoginDTO userOauthLoginDTO){
                UserAuthResponseDTO userLoginResponseDTO = userAuthService.loginWithOauth(userOauthLoginDTO);
                return  new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }


    @PostMapping("login")
    public  ResponseEntity<?>login(@Valid @RequestBody UserLoginDTO userLoginDTO){

           UserAuthResponseDTO userLoginResponseDTO = userAuthService.loginUser(userLoginDTO);
            return  new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }


    @GetMapping("clerk-user")
    public  ResponseEntity<?>clerkUser(@RequestParam String email){
        boolean res  = clerkAuthService.deleteExistingClerkUser(email);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @GetMapping("check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean isPresent = userAuthService.checkEmailExists(email);
        if(isPresent){
          throw new ExistingEntityException("user with this email already exists");
        }
        else {
            clerkAuthService.deleteExistingClerkUser(email);
        }
        return  new ResponseEntity<>(isPresent, HttpStatus.OK);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenRequestDTO request
    ) {

        return ResponseEntity.ok(
                userAuthService.refreshAccessToken(
                        request.getRefreshToken()
                )
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody RefreshTokenRequestDTO request
    ) {

        userAuthService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                "Logged out successfully"
        );
    }

}
