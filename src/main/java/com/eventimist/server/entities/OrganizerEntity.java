package com.eventimist.server.entities;
import jakarta.persistence.*;

import java.util.List;


@Entity
@Table (name = "organizers")
public class OrganizerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false , unique = true)
    private  String email;
    @Column(nullable = false)
    private  String bio;
    @Column(nullable = false)
    private String profile_pic;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEntity> events;
}
