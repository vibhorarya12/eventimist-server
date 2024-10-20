package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.service.OrganizerActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerActionsServiceImplement implements OrganizerActionsService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;

    @Autowired
    public OrganizerActionsServiceImplement(EventRepository eventRepository, OrganizerRepository organizerRepository) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
    }

    @Override
    public EventEntity createEvent(CreateEventDTO createEventDTO) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setTitle(createEventDTO.getTitle());
        eventEntity.setType(createEventDTO.getType());
        eventEntity.setDescription(createEventDTO.getDescription());
        eventEntity.setDate(createEventDTO.getDate());
        eventEntity.setVenue(createEventDTO.getVenue());
        eventEntity.setTags(createEventDTO.getTags());
        eventEntity.setLatitude(createEventDTO.getLatitude());
        eventEntity.setLongitude(createEventDTO.getLongitude());
        eventEntity.setImages(createEventDTO.getImages());
        eventEntity.setAttendance(createEventDTO.getAttendance());

        // Retrieve the OrganizerEntity based on organizerId from the DTO
        OrganizerEntity organizer = organizerRepository.findById(createEventDTO.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        eventEntity.setOrganizer(organizer);

        // Save the event entity in the repository
        return eventRepository.save(eventEntity);
    }
    @Override
    public List<EventEntity> getEventsByOrganizerId(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }
}
