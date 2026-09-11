package com.eventimist.server.repository;

import com.eventimist.server.entities.UserRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository
        extends JpaRepository<UserRefreshTokenEntity, Long> {

    Optional<UserRefreshTokenEntity> findByTokenHash(String tokenHash);

    void deleteAllByUserId(long userId);
}