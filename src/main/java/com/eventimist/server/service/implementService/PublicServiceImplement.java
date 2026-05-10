package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.publicDTO.DiscoverEventResponseDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventsRequestDTO;
import com.eventimist.server.dto.publicDTO.ViewEventResponseDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import com.eventimist.server.exceptions.BadRequestException;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.NearbyEventProjection;
import com.eventimist.server.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImplement implements PublicService {

    private final EventRepository eventRepository;

    @Override
    public List<DiscoverEventResponseDTO> discoverEvents(DiscoverEventsRequestDTO dto) {

        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new RuntimeException("Latitude and Longitude are required");
        }

        double radiusKm = dto.getRadius() != null ? dto.getRadius() : 10.0;
        double radiusMeters = radiusKm * 1000;

        List<NearbyEventProjection> results = eventRepository.findNearbyEvents(
                dto.getLatitude(),
                dto.getLongitude(),
                radiusMeters
        );

        return results.stream().map(row -> {

            DiscoverEventResponseDTO res = new DiscoverEventResponseDTO();

            res.setId(row.getId());
            res.setTitle(row.getTitle());
            res.setDescription(row.getDescription());

            res.setCategory(
                    row.getCategory() != null
                            ? EventCategory.valueOf(row.getCategory())
                            : null
            );

            res.setMode(
                    row.getMode() != null
                            ? EventMode.valueOf(row.getMode())
                            : null
            );

            res.setStartTime(row.getStartTime());
            res.setTimezone(row.getTimezone());


            res.setVenue(row.getVenue());

            res.setLatitude(row.getLatitude());
            res.setLongitude(row.getLongitude());

            res.setCoverImage(row.getCoverImage());
            res.setDistance(row.getDistance());

            res.setOrganizerName(row.getOrganizerName());
            res.setOrganizerImage(row.getOrganizerImage());

            res.setSlug(row.getSlug());

            return res;

        }).toList();
    }

    @Override
    public ViewEventResponseDTO getEvent(String slug){

        // Extract ID from slug
        String[] parts = slug.split("-");

        Long eventId;

        try {
            eventId = Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid event slug");
        }


        // Fetch event
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Optional safety check
        if (!event.getSlug().equals(slug)) {
            throw new BadRequestException("Invalid event slug");
        }

        // Map response
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
}