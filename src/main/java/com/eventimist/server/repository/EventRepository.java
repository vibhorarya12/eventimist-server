package com.eventimist.server.repository;

import com.eventimist.server.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findByOrganizerId(Long organizerId);

    @Query(value = """
        SELECT * FROM event 
        WHERE ST_DWithin(
            location,
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
            :distanceInMeters
        )
        ORDER BY ST_Distance(
            location, 
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
        )
        """, nativeQuery = true)
    List<EventEntity> findNearbyEvents(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters
    );
}
