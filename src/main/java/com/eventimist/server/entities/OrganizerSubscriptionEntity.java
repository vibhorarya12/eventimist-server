// src/main/java/com/eventimist/server/entities/OrganizerSubscriptionEntity.java

package com.eventimist.server.entities;

import com.eventimist.server.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizer_subscriptions")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Organizer Relation ─────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private OrganizerEntity organizer;

    // ─── Subscription Plan ─────────────────────────────────────────────
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType = PlanType.FREE;

    // ─── AI Credits ────────────────────────────────────────────────────
    @Builder.Default
    @Column(name = "ai_credits_remaining", nullable = false)
    private Integer aiCreditsRemaining = 10;

    @Builder.Default
    @Column(name = "monthly_ai_credits", nullable = false)
    private Integer monthlyAiCredits = 10;

    // ─── Prompt Character Limit ───────────────────────────────────────
    @Builder.Default
    @Column(name = "prompt_character_limit", nullable = false)
    private Integer promptCharacterLimit = 300;

    // ─── Subscription Dates ───────────────────────────────────────────
    @Builder.Default
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column(name = "end_date")
    private LocalDate endDate;

    // ─── Status ───────────────────────────────────────────────────────
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    // ─── Audit ────────────────────────────────────────────────────────
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── Lifecycle Hooks ──────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}