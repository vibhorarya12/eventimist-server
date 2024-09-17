package com.eventimist.server.controllers;


import com.eventimist.server.dto.LoginDTO;
import com.eventimist.server.dto.RegisterDTO;
import com.eventimist.server.service.AuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("register")
    public ResponseEntity<String>register(@RequestBody RegisterDTO registerDTO){
        try {
            authService.registerUser(registerDTO);
            return  new ResponseEntity<>("User registered sucessfully", HttpStatus.OK);
        }
        catch (Exception e){

            return new ResponseEntity<>("Error creating user", HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("login")
    public  ResponseEntity<String>login(@RequestBody LoginDTO loginDTO){
        boolean isAuthenticated = authService.loginUser(loginDTO);
        if (isAuthenticated) {
            String jwtToken = jwtUtil.generateToken(loginDTO.getEmail());
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
