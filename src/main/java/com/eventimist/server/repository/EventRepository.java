package com.eventimist.server.repository;

import com.eventimist.server.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findByOrganizerId(Long organizerId);

}
