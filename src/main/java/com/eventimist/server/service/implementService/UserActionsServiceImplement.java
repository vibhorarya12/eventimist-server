package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.UserActionsDTO.EventsResponseDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.UserActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
   public List<EventsResponseDTO>getAttendingEvents (Long userId){
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

}
