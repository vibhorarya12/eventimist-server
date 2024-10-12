package com.eventimist.server.controllers;


import com.eventimist.server.dto.organizerDTO.OrganizerLoginDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerLoginResponseDTO;
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

    @PostMapping("login")
    public ResponseEntity<?>login(@RequestBody OrganizerLoginDTO organizerLoginDTO) {
        try{
          OrganizerLoginResponseDTO organizerLoginResponseDTO = organizerAuthService.organizerLogin(organizerLoginDTO);
          return  new ResponseEntity<>(organizerLoginResponseDTO , HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody OrganizerRegisterDTO organizerRegisterDTO){
        try{
         OrganizerRegisterResponseDTO organizerRegisterResponseDTO  = organizerAuthService.registerOrganizer(organizerRegisterDTO);

           return  new ResponseEntity<>(organizerRegisterResponseDTO, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

}
