package com.eventimist.server.repository;


import com.eventimist.server.entities.OrganizerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerRepository extends JpaRepository<OrganizerEntity , Long> {

    Optional<OrganizerEntity> findByEmail(String email);
    Optional<OrganizerEntity> findByNameIgnoreCase(String name);

}
