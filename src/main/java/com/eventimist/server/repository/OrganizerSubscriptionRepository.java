// src/main/java/com/eventimist/server/repository/OrganizerSubscriptionRepository.java

package com.eventimist.server.repository;

import com.eventimist.server.entities.OrganizerSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerSubscriptionRepository
        extends JpaRepository<OrganizerSubscriptionEntity, Long> {

    Optional<OrganizerSubscriptionEntity> findByOrganizerId(Long organizerId);

    boolean existsByOrganizerId(Long organizerId);
}