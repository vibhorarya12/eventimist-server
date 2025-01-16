package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.UserActionsDTO.EventsResponseDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.UserActionsService;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActionsServiceImplement implements UserActionsService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserActionsServiceImplement(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void bookmarkEvents(Long userId, Long eventId) {
        // Fetch the user by ID
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Fetch the event by ID
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Add the event to the user's bookmarked events if not already present
        if (!user.getBookmarkedEvents().contains(event)) {
            user.getBookmarkedEvents().add(event);
        }

        // Save the updated user entity
        userRepository.save(user);
    }

    @Override
    public void attendEvents(Long userId, Long eventId) {
        // Fetch the user by ID
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Fetch the event by ID
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Check if the user is not already attending the event
        if (!user.getAttendingEvents().contains(event)) {
            // Increment the event's attendance count
            event.setAttendance(event.getAttendance() + 1);

            // Add the event to the user's attending events list
            user.getAttendingEvents().add(event);

            // Save the updated event entity to the database
            eventRepository.save(event);

            // Save the updated user entity to the database
            userRepository.save(user);
        }
    }

    @Override
    public List<EventsResponseDTO> getBookmarkedEvents(Long userId) {
        // Fetch the user by ID
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Map the list of bookmarked events to BookmarkedEventDTO
        return user.getBookmarkedEvents().stream()
                .map(event -> {
                    EventsResponseDTO dto = new EventsResponseDTO();
                    dto.setId(event.getId());
                    dto.setTitle(event.getTitle());
                    dto.setType(event.getType());
                    dto.setDescription(event.getDescription());
                    dto.setDate(event.getDate());
                    dto.setVenue(event.getVenue());
                    dto.setImages(event.getImages());
                    dto.setAttendance(event.getAttendance());

                    // organizer info mapping //
                    dto.setOrganizerId(event.getOrganizer().getId());
                    dto.setOrganizerName(event.getOrganizer().getName());
                    dto.setOrganizerProfilePic(event.getOrganizer().getProfile_pic());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EventsResponseDTO> getAttendingEvents(Long userId) {
        // Fetch the user by ID
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Map the list of bookmarked events to BookmarkedEventDTO
        return user.getAttendingEvents().stream()
                .map(event -> {
                    EventsResponseDTO dto = new EventsResponseDTO();
                    dto.setId(event.getId());
                    dto.setTitle(event.getTitle());
                    dto.setType(event.getType());
                    dto.setDescription(event.getDescription());
                    dto.setDate(event.getDate());
                    dto.setVenue(event.getVenue());
                    dto.setAttendance(event.getAttendance());
                    dto.setImages(event.getImages());

                    // organizer info mapping //
                    dto.setOrganizerId(event.getOrganizer().getId());
                    dto.setOrganizerName(event.getOrganizer().getName());
                    dto.setOrganizerProfilePic(event.getOrganizer().getProfile_pic());


                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EventsResponseDTO> getNearbyEvents(double latitude, double longitude, double radiusKm) {
        try {
            // Convert radius from kilometers to meters
            double radiusInMeters = radiusKm * 1000;

            // Fetch nearby events using PostGIS
            List<EventEntity> nearbyEvents = eventRepository.findNearbyEvents(
                    latitude,
                    longitude,
                    radiusInMeters
            );
            System.out.println(nearbyEvents);
            // Convert to DTOs
            return nearbyEvents.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
//            log.error("Error finding nearby events: ", e);
            throw new RuntimeException("Failed to find nearby events", e);
        }
    }

    private EventsResponseDTO convertToDTO(EventEntity event) {
        Point location = event.getLocation();

        // Create a new instance of EventsResponseDTO
        EventsResponseDTO eventsResponseDTO = new EventsResponseDTO();

        // Set values using the setter methods
        eventsResponseDTO.setId(event.getId());
        eventsResponseDTO.setTitle(event.getTitle());
        eventsResponseDTO.setType(event.getType());
        eventsResponseDTO.setDescription(event.getDescription());
        eventsResponseDTO.setDate(event.getDate());
        eventsResponseDTO.setVenue(event.getVenue());
//        eventsResponseDTO.setTags(event.getTags());
        eventsResponseDTO.setLatitude(location.getY());
        eventsResponseDTO.setLongitude(location.getX());
        // Set latitude and longitude
//        eventsResponseDTO.setLatitude(location.getY());  // Latitude is Y coordinate
//        eventsResponseDTO.setLongitude(location.getX()); // Longitude is X coordinate
        eventsResponseDTO.setImages(event.getImages());
        eventsResponseDTO.setAttendance(event.getAttendance());

        // Return the populated EventsResponseDTO
        return eventsResponseDTO;
    }

}