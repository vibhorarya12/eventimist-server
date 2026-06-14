package com.eventimist.server.repository;

import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findByOrganizerId(Long organizerId);

    List<EventEntity> findByOrganizerIdAndStatus(
            Long organizerId,
            EventStatus status
    );

    @Query(value = """
SELECT 
    e.id AS id,
    e.title AS title,
    e.description AS description,
    e.category AS category,
    e.start_time AS startTime,
    e.timezone AS timezone,
    e.mode AS mode,
    e.venue AS venue,
    e.cover_image AS coverImage,
    e.slug AS slug,

    ST_Y(e.location::geometry) AS latitude,
    ST_X(e.location::geometry) AS longitude,

    ST_Distance(
        e.location,
        ST_SetSRID(
            ST_MakePoint(:longitude, :latitude),
            4326
        )::geography
    ) / 1000 AS distance,

    o.name AS organizerName,
    o.profile_pic AS organizerImage

FROM event e
JOIN organizers o
    ON e.organizer_id = o.id

WHERE e.status = 'PUBLISHED'
AND ST_DWithin(
    e.location,
    ST_SetSRID(
        ST_MakePoint(:longitude, :latitude),
        4326
    )::geography,
    :distanceInMeters
)

ORDER BY distance ASC

LIMIT :limit
OFFSET :offset
""", nativeQuery = true)
    List<NearbyEventProjection> findNearbyEvents(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
SELECT COUNT(*)
FROM event e
WHERE e.status = 'PUBLISHED'
AND ST_DWithin(
    e.location,
    ST_SetSRID(
        ST_MakePoint(:longitude, :latitude),
        4326
    )::geography,
    :distanceInMeters
)
""", nativeQuery = true)
    Long countNearbyEvents(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters
    );
}