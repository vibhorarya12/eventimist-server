package com.eventimist.server.controllers;
import com.eventimist.server.dto.organizerDTO.*;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/auth/organizer")
public class OrganizerAuthController {

    @Autowired
    private OrganizerAuthService organizerAuthService;

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody OrganizerLoginDTO organizerLoginDTO) {

        OrganizerLoginResponseDTO organizerLoginResponseDTO = organizerAuthService.organizerLogin(organizerLoginDTO);
        return new ResponseEntity<>(organizerLoginResponseDTO, HttpStatus.OK);

    }

    @PostMapping("register")
    public ResponseEntity<?> register(@Valid @RequestBody OrganizerRegisterDTO organizerRegisterDTO) {

        OrganizerRegisterResponseDTO organizerRegisterResponseDTO = organizerAuthService.registerOrganizer(organizerRegisterDTO);

        return new ResponseEntity<>(organizerRegisterResponseDTO, HttpStatus.OK);

    }

    @PostMapping ("oauthRegister")
    public ResponseEntity<?> oAUthRegister(@Valid @RequestBody OrganizerOauthRegisterDTO oauthRegisterDTO) {

        OrganizerRegisterResponseDTO organizerRegisterResponseDTO = organizerAuthService.registerWithOauth(oauthRegisterDTO);

        return new ResponseEntity<>(organizerRegisterResponseDTO, HttpStatus.OK);

    }

    @PostMapping ("oauthLogin")
    public ResponseEntity<OrganizerLoginResponseDTO> oAUthLogin(@Valid @RequestBody OauthLoginRequestDTO oauthLoginRequestDTO) {


        OrganizerLoginResponseDTO responseDTO = organizerAuthService.loginWithOauth(oauthLoginRequestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(
            @RequestParam(required = false)
            @NotBlank(message = "Email must be provided")
            String email
    ) {

        // Call service to check email existence
        boolean emailExists = organizerAuthService.checkEmailExists(email);

        if (emailExists) {
            return ResponseEntity.ok(Map.of("message", "Email exists", "exists", true));
        } else {
            return ResponseEntity.ok(Map.of("message", "Email not found", "exists", false));
        }
    }


    @GetMapping("/test")
    public ResponseEntity<String> test (){


        throw new EntityNotFoundException("not found bhai");

    }

}





