package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.dto.organizerActionsDTO.GetEventsResponseDTO;
import com.eventimist.server.dto.organizerActionsDTO.UpdateProfileDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.exceptions.BadRequestException;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.exceptions.ImageUploadException;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.service.CloudinaryService;
import com.eventimist.server.service.OrganizerActionsService;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganizerActionsServiceImplement implements OrganizerActionsService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    private final CloudinaryService cloudinaryService;

    @Autowired
    public OrganizerActionsServiceImplement(EventRepository eventRepository, OrganizerRepository organizerRepository, CloudinaryService cloudinaryService) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.cloudinaryService = cloudinaryService;
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
    public EventEntity createEvent(CreateEventDTO createEventDTO, MultipartFile[] files) {
        EventEntity eventEntity = new EventEntity();

        // Set basic details
        eventEntity.setTitle(createEventDTO.getTitle());
        eventEntity.setType(createEventDTO.getType());
        eventEntity.setDescription(createEventDTO.getDescription());

        // Handle date from string to Date object
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date parsedDate = dateFormat.parse(createEventDTO.getDate());
            eventEntity.setDate(parsedDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }

        // Set location as a Point object
        Point location = geometryFactory.createPoint(new Coordinate(createEventDTO.getLongitude(), createEventDTO.getLatitude()));
        eventEntity.setLocation(location);

        // Set other fields
        eventEntity.setVenue(createEventDTO.getVenue());
        eventEntity.setTags(createEventDTO.getTags());

        // Upload each image to Cloudinary and collect the URLs
        List<String> imageUrls = Arrays.stream(files)
                .map(file -> {
                    try {
                        // Upload the file and get the URL
                        return cloudinaryService.CloudinaryImageUpload(file);
                    } catch (Exception e) {
                        throw new ImageUploadException("Failed to upload image");
                    }
                })
                .collect(Collectors.toList());

        // Set the list of image URLs in the event entity
        eventEntity.setImages(imageUrls);

        // Retrieve the OrganizerEntity based on organizerId from the DTO
        OrganizerEntity organizer = organizerRepository.findById(getUserId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        eventEntity.setOrganizer(organizer);

        return eventRepository.save(eventEntity);
    }

    @Override
    public List<GetEventsResponseDTO> getEventsByOrganizerId() {
        // Fetch events for the given organizerId from the repository

        List<EventEntity> events = eventRepository.findByOrganizerId(getUserId());

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
        UpdateProfileDTO responseDTO = new UpdateProfileDTO();
        responseDTO.setName(organizer.getName());
        responseDTO.setBio(organizer.getBio());
        responseDTO.setLocation(organizer.getLocation());

        return responseDTO;
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


    private GetEventsResponseDTO mapToGetEventsResponseDTO(EventEntity event) {
        GetEventsResponseDTO dto = new GetEventsResponseDTO();

        // Set basic details
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setType(event.getType());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setVenue(event.getVenue());
        dto.setTags(event.getTags());

        // Set location coordinates from Point object
        dto.setLatitude(event.getLocation().getY()); // Latitude is Y coordinate
        dto.setLongitude(event.getLocation().getX()); // Longitude is X coordinate
        System.out.println(event.getLocation());

        // Set other fields
        dto.setImages(event.getImages());
        dto.setAttendance(event.getAttendance());

        return dto;
    }
}
