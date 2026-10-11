package com.gigwork.application;

import com.gigwork.gig.Gig;
import com.gigwork.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "gig_applications",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_applicant_gig",
        columnNames = {"applicant_id", "gig_id"}
    )
)
public class GigApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gig_id", nullable = false)
    private Gig gig;

    @Column(nullable = false, length = 30)
    private String status = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
    }

    public Long getId() { return id; }

    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }

    public Gig getGig() { return gig; }
    public void setGig(Gig gig) { this.gig = gig; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
