package com.eventimist.server.controllers;


import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftResponseDTO;
import com.eventimist.server.dto.common.ApiResponseDTO;
import com.eventimist.server.dto.publicDTO.AIDiscoverEventsRequestDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventsRequestDTO;
import com.eventimist.server.exceptions.BadRequestException;
import com.eventimist.server.service.OrganizerAIService;
import com.eventimist.server.service.PublicService;
import com.eventimist.server.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
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
    private OrganizerAIService organizerAiService;

    @Autowired
    private  RateLimiterService rateLimiterService;
    @GetMapping("/health")
    public ResponseEntity<?> checkHealth() {

        rateLimiterService.checkRateLimit(
                "health-test",
                5
        );

        log.info("health status ok....");

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "health check... Success"
                )
        );
    }

    @GetMapping("/discover-events")
    public ResponseEntity<?> discoverEvents(HttpServletRequest request,
            @Valid @ModelAttribute DiscoverEventsRequestDTO dto
    ) {
        log.info("IP is : " + request.getRemoteAddr());
        rateLimiterService.checkRateLimit(
                "discover:" + request.getRemoteAddr(),
                50
        );


        return ResponseEntity.ok(
                publicService.discoverEvents(dto)
        );
    }


    @GetMapping("/event/{slug}")
    public ResponseEntity<?> getEvents(HttpServletRequest request ,@PathVariable String slug){

        rateLimiterService.checkRateLimit(
                "discover:" + request.getRemoteAddr(),
                10
        );
        return ResponseEntity.ok(publicService.getEvent(slug)
        );

    }


    @PostMapping("/discover-events-ai")
    public ResponseEntity<?> discoverEventsByPrompt(
            @Valid @RequestBody
            AIDiscoverEventsRequestDTO requestDTO
    ) {

        return ResponseEntity.ok(
                publicService.discoverEventsByPrompt(
                        requestDTO
                )
        );

    }

//    @PostMapping("/generate-event-draft")
//    public ResponseEntity<GenerateEventDraftResponseDTO> generateEventDraft(
//            @Valid @RequestBody GenerateEventDraftRequestDTO requestDTO
//    ) {
//
//        GenerateEventDraftResponseDTO response =
//                organizerAiService.generateEventDraft(requestDTO);
//
//        return ResponseEntity.ok(response);
//    }


}
