package com.eventimist.server.dto.organizerActionsDTO;

import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EditEventDTO {

    // ---------------- Core Info ----------------
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private EventCategory category;

    // ---------------- Time ----------------
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    // ---------------- Mode & Location ----------------
    @NotNull(message = "Mode is required")
    private EventMode mode;

    private String venue;        // required based on mode
    private String onlineLink;   // required based on mode

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    // ---------------- Tags ----------------
    private List<String> tags;

    // ---------------- Ticketing ----------------
    private Integer capacity;

    private BigDecimal ticketPrice;

    @NotNull(message = "isFree flag is required")
    private Boolean isFree;

    // ---------------- Images ----------------
    private List<String> existingImages;     // URLs to retain

    private MultipartFile[] newImages;       // New uploads
}