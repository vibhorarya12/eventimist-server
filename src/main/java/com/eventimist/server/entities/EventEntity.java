package com.eventimist.server.entities;

import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import com.eventimist.server.enums.EventStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // ---------------- Core Info ----------------
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private OrganizerEntity organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    // ---------------- Time ----------------
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String timezone;

    // ---------------- Location & Mode ----------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventMode mode;

    @Column
    private String venue;

    // ⚠️ DO NOT MODIFY (as requested)
    @Column(nullable = false, columnDefinition = "geography(Point, 4326)")
    private Point location;

    @Column
    private String onlineLink;

    // ---------------- Media ----------------
    @Column(columnDefinition = "TEXT")
    private String coverImage;

    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_url")
    private List<String> images;

    // ---------------- Discovery ----------------
    @ElementCollection
    @CollectionTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = true)
    private String slug;

    // ---------------- Ticketing ----------------
    @Column
    private Integer capacity;

    @Column(precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @Column(nullable = false)
    private Boolean isFree = true;

    // ---------------- Lifecycle ----------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime publishedAt;

    // ---------------- Engagement ----------------
    @Column(nullable = false)
    private long attendance = 0;

    @Column
    private long rsvpCount = 0;
}