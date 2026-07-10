package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.ai.AIEventClassificationResponseDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.enums.EventMode;
import com.eventimist.server.enums.EventStatus;
import com.eventimist.server.repository.EventRepository;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.dto.scrape.ScrapedEventDTO;
import com.eventimist.server.dto.scrape.ScrapedOrganizerDTO;
import com.eventimist.server.service.AIEventClassificationService;
import com.eventimist.server.service.EventImportService;
import com.eventimist.server.service.EventScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;
import org.locationtech.jts.geom.Coordinate;
@Service
@RequiredArgsConstructor
public class EventImportServiceImplement
        implements EventImportService {

    private final EventScraperService eventScraperService;

    private final OrganizerRepository organizerRepository;

    private final EventRepository eventRepository;

    private final PasswordEncoder passwordEncoder;


    private final AIEventClassificationService aiEventClassificationService;
    private final GeometryFactory geometryFactory = new GeometryFactory();


    @Override
    public EventEntity importEvent(
            String url
    ) {


        ScrapedEventDTO scrapedEvent =
                eventScraperService.scrape(url);

        AIEventClassificationResponseDTO aiResponse =
                aiEventClassificationService.classify(
                        scrapedEvent.getTitle(),
                        scrapedEvent.getDescription()
                );

        OrganizerEntity organizer =
                getOrCreateOrganizer(
                        scrapedEvent.getOrganizer()
                );

        EventEntity event =
                new EventEntity();

        event.setTitle(
                scrapedEvent.getTitle()
        );

        event.setCategory(
                aiResponse.getCategory()
        );
        event.setTags(
                aiResponse.getTags()
        );

        event.setDescription(
                scrapedEvent.getDescription()
        );

        event.setCoverImage(
                scrapedEvent.getCoverImage()
        );

        event.setVenue(
                scrapedEvent.getVenue().getVenueName()
                        + ", "
                        + scrapedEvent.getVenue().getAddress()
        );

       Point location = geometryFactory.createPoint(
               new Coordinate(
                       scrapedEvent.getVenue().getLongitude(),
                       scrapedEvent.getVenue().getLatitude()
               )
       );

       event.setLocation(location);

        event.setIsFree(
                scrapedEvent.getFree()
        );

        event.setStartTime(
                scrapedEvent.getStartTime()
        );

        event.setEndTime(
                scrapedEvent.getEndTime()
        );

        event.setTimezone(
                scrapedEvent.getTimezone()
        );

        event.setOrganizer(
                organizer
        );

        event.setMode(
                EventMode.OFFLINE
        );

        event.setStatus(
                EventStatus.PUBLISHED
        );



        LocalDateTime now = LocalDateTime.now();
        event.setMode(EventMode.OFFLINE);
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        event.setPublishedAt(now);


//        System.out.println("Cover length = " + event.getCoverImage().length());
//        System.out.println("Venue length = " + event.getVenue().length());
//        System.out.println("Title length = " + event.getTitle().length());
//        System.out.println("Slug length = " + event.getSlug());


        EventEntity savedEvent =
                eventRepository.save(event);

        savedEvent.setSlug(
                generateSlug(
                        savedEvent.getTitle(),
                        savedEvent.getId()
                )
        );


        return eventRepository.save(savedEvent);


    }

    private OrganizerEntity getOrCreateOrganizer(
            ScrapedOrganizerDTO scrapedOrganizer
    ) {

        return organizerRepository
                .findByNameIgnoreCase(
                        scrapedOrganizer.getName()
                )
                .orElseGet(() -> {

                    OrganizerEntity organizer =
                            new OrganizerEntity();

                    organizer.setName(
                            scrapedOrganizer.getName()
                    );

                    organizer.setBio(
                            scrapedOrganizer.getBio()
                    );

                    organizer.setProfile_pic(
                            scrapedOrganizer.getProfileImage()
                    );

                    organizer.setEmail(
                            generateEmail(
                                    scrapedOrganizer.getName()
                            )
                    );

                    organizer.setPassword(
                            passwordEncoder.encode(
                                    UUID.randomUUID().toString()
                            )
                    );

                    return organizerRepository.save(
                            organizer
                    );

                });
    }

    private String generateEmail(
            String name
    ) {

        return name
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                + "@eventimist.import";
    }

    private String generateSlug(
            String title,
            long id
    ) {

        return title
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "")
                + "-"
                + id;
    }

}