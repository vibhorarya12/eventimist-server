package com.eventimist.server.controllers;

import com.cloudinary.Cloudinary;
import com.eventimist.server.dto.ai.AIChatRequestDTO;
import com.eventimist.server.dto.ai.AIChatResponseDTO;
import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.common.ApiResponseDTO;
import com.eventimist.server.dto.organizerActionsDTO.*;
import com.eventimist.server.service.OrganizerAIService;
import com.eventimist.server.service.OrganizerActionsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/organizer")
public class OrganizerActionsControllers {

    @Autowired
    private OrganizerActionsService organizerActionsService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private OrganizerAIService organizerAiService;

    @PostMapping("check")
    public ResponseEntity<?> check(@RequestPart("files") MultipartFile[] files) {
        try {
            // Collect all file names
            List<String> fileNames = Arrays.stream(files)
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(fileNames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing files", HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("create-event")
    public ResponseEntity<?> createEvent(@Valid @ModelAttribute CreateEventDTO createEventDTO,
                                         @RequestPart("files") MultipartFile[] files) {
        organizerActionsService.createEvent(createEventDTO, files);

        // Return a success response
        return new ResponseEntity<>(createEventDTO, HttpStatus.CREATED);
    }

    // get all events by organizer ID //
    @GetMapping("/events")
    public ResponseEntity<?> getEvents() {

        List<GetEventsResponseDTO> events = organizerActionsService.getEventsByOrganizerId();

        return ResponseEntity.ok(events);
    }


    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
           @Valid @ModelAttribute EditEventDTO editEventDTO
    ) {

        organizerActionsService.updateEvent(id, editEventDTO);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Event updated successfully")
        );
    }


    @PatchMapping("/events/{id}/publish")
    public ResponseEntity<?> publishEvent(@PathVariable Long id) {

        organizerActionsService.publishEvent(id);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Event published successfully")
        );
    }

    @PatchMapping("update-profile-info")
    public ResponseEntity<?> updateProfileInfo(@Valid @RequestBody UpdateProfileDTO updateProfileDTO){

        UpdateProfileDTO responseDTO  = organizerActionsService.updateProfileInfo(updateProfileDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }
    @PatchMapping("update-image")
    public ResponseEntity<Map<String, String>> updateImage(
            @RequestPart("image") MultipartFile file,
            @RequestParam("type") String type) {

        String imageUrl = organizerActionsService.UpdateImage(file, type);

        return ResponseEntity.ok(Collections.singletonMap("imageUrl", imageUrl));

    }


    @PostMapping("/generate-event-draft")
    public ResponseEntity<AIEventDraftResponseDTO> generateEventDraft(
            @Valid @RequestBody GenerateEventDraftRequestDTO requestDTO
    ) {

        AIEventDraftResponseDTO response =
                organizerAiService.generateEventDraft(requestDTO);


        return ResponseEntity.ok(response);
    }

    @PostMapping("/ai-chat")
    public ResponseEntity<AIChatResponseDTO> chat(
            @RequestBody AIChatRequestDTO request
    ) {

        AIChatResponseDTO response =
                organizerAiService.chat(request.getPrompt());

        return ResponseEntity.ok(response);
    }

   @GetMapping("/subscriptions")
   public ResponseEntity<OrganizerSubscriptionResponseDTO> getSubscription(){
        OrganizerSubscriptionResponseDTO response = organizerActionsService.getOrganizerSubscription();

        return  ResponseEntity.ok(response);
   }


    @PostMapping("upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return new ResponseEntity<>(uploadResult, HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception by returning an appropriate error response
            return new ResponseEntity<>("Failed to upload image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


