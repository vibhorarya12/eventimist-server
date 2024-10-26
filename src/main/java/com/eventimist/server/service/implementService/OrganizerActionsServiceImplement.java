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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizerActionsServiceImplement implements OrganizerActionsService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    public OrganizerActionsServiceImplement(EventRepository eventRepository, OrganizerRepository organizerRepository) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
    }

    @Override
    public EventEntity createEvent(CreateEventDTO createEventDTO, MultipartFile[] files) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setTitle(createEventDTO.getTitle());
        eventEntity.setType(createEventDTO.getType());
        eventEntity.setDescription(createEventDTO.getDescription());

        //handle date from string to date //
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date parsedDate = dateFormat.parse(createEventDTO.getDate());
            eventEntity.setDate(parsedDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }

        eventEntity.setVenue(createEventDTO.getVenue());
        eventEntity.setTags(createEventDTO.getTags());
        eventEntity.setLatitude(createEventDTO.getLatitude());
        eventEntity.setLongitude(createEventDTO.getLongitude());
        eventEntity.setAttendance(createEventDTO.getAttendance());
        // Upload each image to Cloudinary and collect the URLs
        List<String> imageUrls = Arrays.stream(files)
                .map(file -> {
                    try {
                        // Upload the file and get the URL
                        return cloudinaryService.CloudinaryImageUpload(file);
                    } catch (Exception e) {
                        throw new ImageUploadException("failed to upload image");
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
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setType(event.getType());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setVenue(event.getVenue());
        dto.setTags(event.getTags());
        dto.setLatitude(event.getLatitude());
        dto.setLongitude(event.getLongitude());
        dto.setImages(event.getImages());
        dto.setAttendance(event.getAttendance());
        return dto;
    }
}
