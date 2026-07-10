package com.eventimist.server.controllers;


import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftResponseDTO;
import com.eventimist.server.dto.common.ApiResponseDTO;
import com.eventimist.server.dto.geocoding.GeocodingResponseDTO;
import com.eventimist.server.dto.publicDTO.AIDiscoverEventsRequestDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventsRequestDTO;
import com.eventimist.server.dto.scrape.ScrapedEventDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.exceptions.BadRequestException;
import com.eventimist.server.service.*;
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

    @Autowired
    private  EventScraperService eventScraperService;

    @Autowired
    private  EventImportService eventImportService;

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


    @PostMapping("/scrape-event")
    public ResponseEntity<?> scrapeEvent(
            @RequestParam String url
    ) {


        return ResponseEntity.ok(
                eventScraperService.scrape(url)
        );
    }

    @PostMapping("/import-event")
    public ResponseEntity<?> importEvent(
            @RequestParam String url
    ) {

        EventEntity event =
                eventImportService.importEvent(url);

        return ResponseEntity.ok(
                ApiResponseDTO.success(

                        "Event imported successfully"
                )
        );
    }


    @Autowired
    private GeocodingService geocodingService;

    @GetMapping("/test-geocode")
    public ResponseEntity<?> testGeocode() {

        GeocodingResponseDTO dto =
                geocodingService.geocode(
                        "Santa Clara Convention Center, Santa Clara, CA, US"
                );

        return ResponseEntity.ok(dto);
    }


}
