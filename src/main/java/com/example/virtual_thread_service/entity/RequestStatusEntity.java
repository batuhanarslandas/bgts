package com.example.virtual_thread_service.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "request_status")
public class RequestStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private final String payload;

    @Enumerated(EnumType.STRING)
    private final Status status;

    private final String details;

    @Column(nullable = false)
    private final Instant createdAt;

    protected RequestStatusEntity() {
        this.id = null;
        this.payload = null;
        this.status = null;
        this.details = null;
        this.createdAt = null;
    }

    public RequestStatusEntity(Long id, String payload, Status status, String details, Instant createdAt) {
        this.id = id;
        this.payload = payload;
        this.status = status;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getPayload() { return payload; }
    public Status getStatus() { return status; }
    public Status getDetails() { return status; }
    public Instant getCreatedAt() { return createdAt; }


    public enum Status {
        PENDING("PENDING"),
        PROCESSING("PROCESSING"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public RequestStatusEntity withStatus(Status newStatus, String description) {
        return new RequestStatusEntity(this.id, this.payload, newStatus, description, this.createdAt);
    }
}
