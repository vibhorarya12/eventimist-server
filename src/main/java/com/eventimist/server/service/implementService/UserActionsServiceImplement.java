package com.eventimist.server.service.implementService;

import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.UserActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
