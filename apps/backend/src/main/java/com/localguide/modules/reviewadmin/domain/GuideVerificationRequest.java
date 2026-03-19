package com.localguide.modules.reviewadmin.domain;

import com.localguide.modules.authuser.domain.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "guide_verification_requests")
public class GuideVerificationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id", nullable = false, unique = true)
    private User guide;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GuideVerificationStatus status;

    @Column(name = "document_url", nullable = false, length = 500)
    private String documentUrl;

    @Column(name = "submission_note", length = 1000)
    private String submissionNote;

    @Column(name = "review_note", length = 1000)
    private String reviewNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = GuideVerificationStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getGuide() { return guide; }
    public void setGuide(User guide) { this.guide = guide; }
    public GuideVerificationStatus getStatus() { return status; }
    public void setStatus(GuideVerificationStatus status) { this.status = status; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    public String getSubmissionNote() { return submissionNote; }
    public void setSubmissionNote(String submissionNote) { this.submissionNote = submissionNote; }
    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }
    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
