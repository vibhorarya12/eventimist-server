package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.organizerActionsDTO.*;
import com.eventimist.server.dto.publicDTO.ViewEventResponseDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.OrganizerSubscriptionEntity;
import com.eventimist.server.enums.EventStatus;
import com.eventimist.server.exceptions.BadRequestException;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.exceptions.ImageUploadException;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.repository.OrganizerSubscriptionRepository;
import com.eventimist.server.service.EventCacheService;
import com.eventimist.server.service.OrganizerAIService;
import com.eventimist.server.service.CloudinaryService;
import com.eventimist.server.service.OrganizerActionsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import com.eventimist.server.ai.dto.AIEventSummaryDTO;

@Slf4j
@Service
public class OrganizerActionsServiceImplement implements OrganizerActionsService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;


    private final EventCacheService eventCacheService;

    @Autowired
    private final OrganizerSubscriptionRepository organizerSubscriptionRepository;
    @Autowired
    public OrganizerActionsServiceImplement(EventRepository eventRepository, OrganizerRepository organizerRepository, CloudinaryService cloudinaryService , OrganizerSubscriptionRepository organizerSubscriptionRepository , EventCacheService eventCacheService , ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.cloudinaryService = cloudinaryService;
        this.organizerSubscriptionRepository = organizerSubscriptionRepository;
        this.eventCacheService = eventCacheService;
        this.objectMapper = objectMapper;
    }



    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Long getUserId() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new IllegalStateException("Principal is not of type Long");
    }

    @Override
    public EventEntity createEvent(CreateEventDTO dto, MultipartFile[] files) {

        EventEntity event = new EventEntity();

        // ---------------- Core Info ----------------
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setCategory(dto.getCategory());

        // ---------------- Time ----------------
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setTimezone(dto.getTimezone());

        // ---------------- Mode & Validation ----------------
        event.setMode(dto.getMode());

        switch (dto.getMode()) {
            case ONLINE:
                if (dto.getOnlineLink() == null || dto.getOnlineLink().isBlank()) {
                    throw new RuntimeException("Online link is required for ONLINE events");
                }
                break;

            case OFFLINE:
                if (dto.getVenue() == null || dto.getVenue().isBlank()) {
                    throw new RuntimeException("Venue is required for OFFLINE events");
                }
                break;

            case HYBRID:
                if (dto.getVenue() == null || dto.getVenue().isBlank()
                        || dto.getOnlineLink() == null || dto.getOnlineLink().isBlank()) {
                    throw new RuntimeException("Both venue and online link are required for HYBRID events");
                }
                break;
        }

        event.setVenue(dto.getVenue());
        event.setOnlineLink(dto.getOnlineLink());

        // ---------------- Location ----------------
        Point location = geometryFactory.createPoint(
                new Coordinate(dto.getLongitude(), dto.getLatitude())
        );
        event.setLocation(location);


        // ---------------- Media ----------------
        List<String> imageUrls = new ArrayList<>();
        if (files.length > 5) {
            throw new RuntimeException("Maximum 5 images allowed");
        }
        if (files != null && files.length > 0) {

            int threadCount = Math.min(files.length, 5);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            try {
                List<CompletableFuture<String>> futures = Arrays.stream(files)
                        .map(file -> CompletableFuture.supplyAsync(() -> {
                            try {
                                return cloudinaryService.CloudinaryImageUpload(file);
                            } catch (Exception e) {
                                throw new ImageUploadException("Failed to upload image");
                            }
                        }, executor))
                        .toList();

                imageUrls = futures.stream()
                        .map(CompletableFuture::join) // wait for all uploads
                        .toList();

            } finally {
                executor.shutdown(); // ⚠ very important
            }
        }

        event.setImages(imageUrls);

// Optional: first image as cover
        if (!imageUrls.isEmpty()) {
            event.setCoverImage(imageUrls.get(0));
        }
        // ---------------- Discovery ----------------
        event.setTags(dto.getTags());

        // TODO: generate slug (you can implement later)
        // event.setSlug(generateSlug(dto.getTitle()));

        // ---------------- Ticketing ----------------
        event.setCapacity(dto.getCapacity());
//        event.setTicketPrice(dto.getTicketPrice());
//        event.setIsFree(dto.getIsFree());

        // ---------------- Lifecycle ----------------

        event.setStatus(EventStatus.DRAFT);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // ---------------- Organizer ----------------
        OrganizerEntity organizer = organizerRepository.findById(getUserId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        event.setOrganizer(organizer);

        return eventRepository.save(event);
    }



    @Override
    public List<AIEventSummaryDTO> getEventsByStatus(
            EventStatus status
    ) {

        List<EventEntity> events =
                eventRepository.findByOrganizerIdAndStatus(
                        getUserId(),
                        status
                );

        return events.stream()
                .map(event -> AIEventSummaryDTO.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .slug(event.getSlug())
                        .coverImage(event.getCoverImage())
                        .status(event.getStatus().name())
                        .build())
                .toList();
    }




    @Override
    public EventEntity updateEvent(Long id, EditEventDTO dto) {

        // 1️ Fetch existing event
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2️ Validate organizer ownership
        if (event.getOrganizer().getId() != getUserId()) {
            throw new RuntimeException("Unauthorized to edit this event");
        }

        // 3⃣ Basic fields
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setCategory(dto.getCategory());

        // 4️ Time
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setTimezone(dto.getTimezone());

        // 5️ Mode & Location
        event.setMode(dto.getMode());
        event.setVenue(dto.getVenue());
        event.setOnlineLink(dto.getOnlineLink());

        Point location = geometryFactory.createPoint(
                new Coordinate(dto.getLongitude(), dto.getLatitude())
        );
        event.setLocation(location);

        // 6️ Tags
        if (dto.getTags() != null) {
            event.setTags(
                    dto.getTags().stream()
                            .map(tag -> tag.toLowerCase().trim())
                            .collect(Collectors.toList())
            );
        }

        // 7️ Ticketing
        event.setCapacity(dto.getCapacity());
        event.setIsFree(dto.getIsFree());

        if (Boolean.FALSE.equals(dto.getIsFree())) {
            event.setTicketPrice(dto.getTicketPrice());
        } else {
            event.setTicketPrice(null);
        }

        // 8️ Images (clean call)
        handleImages(event, dto);


        // 9️ Timestamp
        event.setUpdatedAt(LocalDateTime.now());

// 10️ Save
        EventEntity savedEvent = eventRepository.save(event);

// 11️ Update Redis cache
        ViewEventResponseDTO responseDTO = mapToViewEventResponse(savedEvent);

        try {
            String eventJson = objectMapper.writeValueAsString(responseDTO);

            eventCacheService.cacheEvent(
                    savedEvent.getSlug(),
                    eventJson
            );

        } catch (JsonProcessingException e) {
            // Don't fail update if Redis serialization fails
        }

        return savedEvent;
    }


    private ViewEventResponseDTO mapToViewEventResponse(EventEntity event) {

        ViewEventResponseDTO dto = new ViewEventResponseDTO();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());

        dto.setCategory(event.getCategory());

        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setTimezone(event.getTimezone());

        dto.setMode(event.getMode());
        dto.setVenue(event.getVenue());

        if (event.getLocation() != null) {
            dto.setLatitude(event.getLocation().getY());
            dto.setLongitude(event.getLocation().getX());
        }

        dto.setCoverImage(event.getCoverImage());
        dto.setImages(event.getImages());

        dto.setTags(event.getTags());

        dto.setAttendance(event.getAttendance());
        dto.setRsvpCount(event.getRsvpCount());

        dto.setIsFree(event.getIsFree());

        if (!event.getIsFree()) {
            dto.setTicketPrice(event.getTicketPrice());
        }

        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getName());
        dto.setOrganizerImage(event.getOrganizer().getProfile_pic());

        dto.setSlug(event.getSlug());

        return dto;
    }




    private void handleImages(EventEntity event, EditEventDTO dto) {

        //  Check if user even wants to update images
        boolean hasExisting = dto.getExistingImages() != null;
        boolean hasNew = dto.getNewImages() != null && dto.getNewImages().length > 0;

        if (!hasExisting && !hasNew) {
            return; //  no change
        }

        //  Step 1: Calculate counts
        int existingCount = hasExisting
                ? dto.getExistingImages().size()
                : event.getImages().size();

        int newCount = hasNew
                ? dto.getNewImages().length
                : 0;

        //  Step 2: Validate limit
        if (existingCount + newCount > 5) {
            throw new RuntimeException("Maximum 5 images allowed");
        }

        //  Step 3: Prepare final list
        List<String> finalImages = new ArrayList<>();

        // retain existing
        if (hasExisting) {
            finalImages.addAll(dto.getExistingImages());
        } else {
            finalImages.addAll(event.getImages());
        }

        //  Step 4: Upload new images
        if (hasNew) {
            List<String> newUrls = Arrays.stream(dto.getNewImages()).parallel()
                    .map(file -> {
                        try {
                            return cloudinaryService.CloudinaryImageUpload(file);
                        } catch (Exception e) {
                            throw new RuntimeException("Image upload failed");
                        }
                    })
                    .collect(Collectors.toList());

            finalImages.addAll(newUrls);
        }

        //  FIX: Sync cover image (ADD HERE)
        if (finalImages == null || finalImages.isEmpty()) {
            event.setCoverImage(null);
        } else {
            if (event.getCoverImage() == null || !finalImages.contains(event.getCoverImage())) {
                event.setCoverImage(finalImages.get(0));
            }
        }
        // Step 5: Update entity
        event.setImages(finalImages);
    }


    @Override
    public List<GetEventsResponseDTO> getEventsByOrganizerId() {
        // Fetch events for the given organizerId from the repository

        List<EventEntity> events = eventRepository.findByOrganizerId(getUserId());
        // Return empty list instead of null (safe default)
        if (events == null || events.isEmpty()) {
            return List.of();
        }
        // Map each EventEntity to GetEventsResponseDTO
        return events.stream()
                .map(this::mapToGetEventsResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UpdateProfileDTO updateProfileInfo(UpdateProfileDTO updateProfileDTO) {
        Long userId = getUserId(); // Assume this retrieves the authenticated user's ID

        Optional<OrganizerEntity> optionalOrganizer = organizerRepository.findById(userId);

        if (optionalOrganizer.isEmpty()) {
            throw new RuntimeException("Organizer not found");
        }

        OrganizerEntity organizer = optionalOrganizer.get();

        // Update only non-null fields
        if (updateProfileDTO.getName() != null) {
            organizer.setName(updateProfileDTO.getName());
        }
        if (updateProfileDTO.getBio() != null) {
            organizer.setBio(updateProfileDTO.getBio());
        }
        if (updateProfileDTO.getLocation() != null) {
            organizer.setLocation(updateProfileDTO.getLocation());
        }

        organizerRepository.save(organizer);

        // Return the updated values as DTO


        return updateProfileDTO;
    }

    @Override
    public String UpdateImage(MultipartFile file, String type) {
        Set<String> allowedTypes = Set.of("profile", "cover");
                if (!allowedTypes.contains(type)) {
            throw new BadRequestException("Please provide type either 'profile' or 'cover'");
        }
        OrganizerEntity organizer = organizerRepository.findById(getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));

         String imageUrl = cloudinaryService.CloudinaryImageUpload(file);

         if ("profile".equals(type)) {
            organizer.setProfile_pic(imageUrl);
        } else {
            organizer.setCover_image(imageUrl);
        }
        organizerRepository.save(organizer);
        return imageUrl;
    }


    // publish events
    @Override
    public void publishEvent(Long eventId) {
        LocalDateTime now = LocalDateTime.now();
        // 1️ Fetch event
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!Objects.equals(event.getOrganizer().getId(), getUserId())) {
            throw new RuntimeException("Unauthorized to publish this event");
        }


// Event completely in past
        if (event.getEndTime().isBefore(now)) {
            throw new RuntimeException("Cannot publish an event that has already ended");
        }

// Invalid time range
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new RuntimeException("Start time cannot be after end time");
        }


        if (event.getStatus() == EventStatus.PUBLISHED) {
            throw new RuntimeException("Event is already published");
        }



        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new RuntimeException("Title is required");
        }

        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new RuntimeException("Description is required");
        }

        if (event.getCategory() == null) {
            throw new RuntimeException("Category is required");
        }

        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new RuntimeException("Start and End time are required");
        }

        if (event.getMode() == null) {
            throw new RuntimeException("Event mode is required");
        }

        // Mode-based validation
        switch (event.getMode()) {
            case ONLINE:
                if (event.getOnlineLink() == null || event.getOnlineLink().isBlank()) {
                    throw new RuntimeException("Online link is required for ONLINE events");
                }
                break;

            case OFFLINE:
                if (event.getVenue() == null || event.getVenue().isBlank()) {
                    throw new RuntimeException("Venue is required for OFFLINE events");
                }
                if (event.getLocation() == null) {
                    throw new RuntimeException("Location is required for OFFLINE events");
                }
                break;

            case HYBRID:
                if (event.getVenue() == null || event.getVenue().isBlank()
                        || event.getOnlineLink() == null || event.getOnlineLink().isBlank()) {
                    throw new RuntimeException("Both venue and online link are required for HYBRID events");
                }
                break;
        }

        // Images validation
        if (event.getImages() == null || event.getImages().isEmpty()) {
            throw new RuntimeException("At least one image is required");
        }

        // Ticketing validation
        if (event.getCapacity() == null || event.getCapacity() <= 0) {
            throw new RuntimeException("Valid capacity is required");
        }

        if (Boolean.FALSE.equals(event.getIsFree())) {
            if (event.getTicketPrice() == null || event.getTicketPrice().doubleValue() <= 0) {
                throw new RuntimeException("Ticket price must be set for paid events");
            }
        }

        // 5 Generate slug with id
        String slug = event.getTitle()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                + "-" + event.getId();

        event.setSlug(slug);

        // 6️ Update status
        event.setStatus(EventStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // 7️ Save
        eventRepository.save(event);
    }





    @Override
    public OrganizerSubscriptionResponseDTO getOrganizerSubscription() {

        Long organizerId = getUserId();
        OrganizerSubscriptionEntity subscription =
                organizerSubscriptionRepository
                        .findByOrganizerId(organizerId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Subscription not found"));

        OrganizerSubscriptionResponseDTO response =
                new OrganizerSubscriptionResponseDTO();

        response.setPlanType(subscription.getPlanType());
        response.setAiCreditsRemaining(subscription.getAiCreditsRemaining());
        response.setMonthlyAiCredits(subscription.getMonthlyAiCredits());
        response.setPromptCharacterLimit(subscription.getPromptCharacterLimit());
        response.setActive(subscription.getActive());

        return response;
    }


    private GetEventsResponseDTO mapToGetEventsResponseDTO(EventEntity event) {

        GetEventsResponseDTO dto = new GetEventsResponseDTO();

        // ---------------- Basic Info ----------------
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setCategory(event.getCategory());

        // ---------------- Time ----------------
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setTimezone(event.getTimezone());

        // ---------------- Location & Mode ----------------
        dto.setMode(event.getMode());
        dto.setVenue(event.getVenue());
        dto.setOnlineLink(event.getOnlineLink());

        // Point → lat/lng (safe)
        if (event.getLocation() != null) {
            dto.setLatitude(event.getLocation().getY());
            dto.setLongitude(event.getLocation().getX());
        }

        // ---------------- Media (NULL SAFE) ----------------
        dto.setCoverImage(event.getCoverImage());

        dto.setImages(
                event.getImages() != null ? event.getImages() : List.of()
        );

        // ---------------- Discovery (NULL SAFE) ----------------
        dto.setTags(
                event.getTags() != null ? event.getTags() : List.of()
        );

        dto.setSlug(event.getSlug());

        // ---------------- Ticketing ----------------
        dto.setCapacity(event.getCapacity());
        dto.setTicketPrice(event.getTicketPrice());
        dto.setIsFree(event.getIsFree());

        // ---------------- Lifecycle ----------------
        dto.setStatus(event.getStatus());

        // ---------------- Engagement ----------------
        dto.setAttendance(event.getAttendance());
        dto.setRsvpCount(event.getRsvpCount());

        return dto;
    }
}
