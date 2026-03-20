package com.localguide.modules.reviewadmin.domain;

import com.localguide.modules.authuser.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_review_booking", columnNames = {"booking_reference"})
        })
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true, length = 100)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tourist_id", nullable = false)
    private User tourist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id", nullable = false)
    private User guide;

    @Column(name = "tour_id")
    private Long tourId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1500)
    private String comment;

    @Column(name = "guide_reply", length = 1500)
    private String guideReply;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public User getTourist() { return tourist; }
    public void setTourist(User tourist) { this.tourist = tourist; }
    public User getGuide() { return guide; }
    public void setGuide(User guide) { this.guide = guide; }
    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getGuideReply() { return guideReply; }
    public void setGuideReply(String guideReply) { this.guideReply = guideReply; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
