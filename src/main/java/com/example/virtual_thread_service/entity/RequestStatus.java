package com.example.virtual_thread_service.entity;

import jakarta.persistence.*;

@Entity
public class RequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String details;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }


    public enum Status {
        PENDING("PENDING"),
        PROCESSING("Processing"),
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
}
