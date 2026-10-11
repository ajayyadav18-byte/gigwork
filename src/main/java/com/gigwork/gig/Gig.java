
package com.gigwork.gig;

import com.gigwork.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gigs")
public class Gig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter a gig title.")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank(message = "Please describe the gig.")
    @Size(max = 5000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Please select a category.")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String category;

    @Size(max = 120)
    @Column(length = 120)
    private String location = "Remote";

    @NotNull(message = "Please enter a budget.")
    @DecimalMin(value = "1.00",
            message = "The minimum budget is ₹1.")
    @DecimalMax(value = "1000000.00",
            message = "The maximum budget per gig is ₹10,00,000.")
    @Digits(integer = 7, fraction = 2,
            message = "Enter a valid budget.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User creator;

    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Gig() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = "OPEN";
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
