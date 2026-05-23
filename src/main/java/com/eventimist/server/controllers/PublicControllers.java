package com.eventimist.server.controllers;


import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.common.ApiResponseDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventResponseDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventsRequestDTO;
import com.eventimist.server.dto.publicDTO.ViewEventResponseDTO;
import com.eventimist.server.service.AIService;
import com.eventimist.server.service.PublicService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicControllers {

    @Autowired
    private PublicService publicService;

    @Autowired
    private  AIService aiService;

    @GetMapping("/health")
    public  ResponseEntity<?>checkHealth(){
        log.info("health status ok....");

        log.warn("Invalid radius received");

        log.error("Error while fetching events");

        return ResponseEntity.ok(
                ApiResponseDTO.success("health check... Success")
        );
    }


    @GetMapping("/discover-events")
    public ResponseEntity<?> discoverEvents(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false, defaultValue = "10") double radius
    ) {
        DiscoverEventsRequestDTO dto = new DiscoverEventsRequestDTO();
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setRadius(radius);

        return ResponseEntity.ok(
                publicService.discoverEvents(dto)
        );
    }
    @GetMapping("/event/{slug}")
    public ResponseEntity<?> getEvents(@PathVariable String slug){
        return ResponseEntity.ok(publicService.getEvent(slug)
        );

    }


    @PostMapping("/generate-event-draft")
    public ResponseEntity<AIEventDraftResponseDTO> generateEventDraft(
            @Valid @RequestBody GenerateEventDraftRequestDTO requestDTO
    ) {

        AIEventDraftResponseDTO response =
                aiService.generateEventDraft(requestDTO);

        return ResponseEntity.ok(response);
    }


}
