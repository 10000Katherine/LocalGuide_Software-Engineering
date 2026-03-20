package com.localguide.modules.reviewadmin.domain;

import com.localguide.modules.authuser.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_favorite_tourist_guide", columnNames = {"tourist_id", "guide_id"})
        })
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tourist_id", nullable = false)
    private User tourist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id", nullable = false)
    private User guide;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getTourist() { return tourist; }
    public void setTourist(User tourist) { this.tourist = tourist; }
    public User getGuide() { return guide; }
    public void setGuide(User guide) { this.guide = guide; }
    public Instant getCreatedAt() { return createdAt; }
}
