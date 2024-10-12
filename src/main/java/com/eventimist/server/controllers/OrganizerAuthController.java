package com.eventimist.server.controllers;


import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterResponseDTO;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/organizer")
public class OrganizerAuthController {
    @Autowired
   private OrganizerAuthService organizerAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("login")
    public ResponseEntity<?>login(){
        String Jwt = jwtUtil.generateToken("this@gmail.com");
        return new ResponseEntity<>("you are logged in " + Jwt,HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody OrganizerRegisterDTO organizerRegisterDTO){
        try{
            organizerAuthService.registerOrganizer(organizerRegisterDTO);
            String Jwt = jwtUtil.generateToken(organizerRegisterDTO.getEmail());
            OrganizerRegisterResponseDTO response = new OrganizerRegisterResponseDTO();
            response.setToken(Jwt);
            response.setEmail(organizerRegisterDTO.getEmail());
            response.setName(organizerRegisterDTO.getName());
            response.setBio(organizerRegisterDTO.getBio());
            response.setProfilePic(organizerRegisterDTO.getProfilePic());
            return  new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("Error creating organizer", HttpStatus.BAD_REQUEST);

        }
    }

}
