package com.eventimist.server.repository;

import com.eventimist.server.entities.OrganizerRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerRefreshTokenRepository
        extends JpaRepository<OrganizerRefreshTokenEntity, Long> {

    Optional<OrganizerRefreshTokenEntity> findByTokenHash(String tokenHash);
}