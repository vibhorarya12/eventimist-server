package com.eventimist.server.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profilePic;

    // Many-to-Many relationship for attending events
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_attending_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<EventEntity> rsvpEvents;

    // Many-to-Many relationship for bookmarked events
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_bookmarked_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<EventEntity> bookmarkedEvents;

    @Column(nullable = false)
    private boolean onboardingCompleted = false;

}
