package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.dto.organizerActionsDTO.GetEventsResponseDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        eventEntity.setAttendance(createEventDTO.getAttendance());

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
        OrganizerEntity organizer = organizerRepository.findById(createEventDTO.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        eventEntity.setOrganizer(organizer);

        return eventRepository.save(eventEntity);
    }

    @Override
    public List<GetEventsResponseDTO> getEventsByOrganizerId(Long organizerId) {
        // Fetch events for the given organizerId from the repository
        List<EventEntity> events = eventRepository.findByOrganizerId(organizerId);

        // Map each EventEntity to GetEventsResponseDTO
        return events.stream()
                .map(this::mapToGetEventsResponseDTO)
                .collect(Collectors.toList());
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
