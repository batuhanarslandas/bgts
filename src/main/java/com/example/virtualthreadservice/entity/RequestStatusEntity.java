package com.example.virtualthreadservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "request_status")
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
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


    @Getter
    public enum Status {
        PENDING("PENDING"),
        PROCESSING("PROCESSING"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

    }

    public RequestStatusEntity withStatus(Status newStatus, String description) {
        return RequestStatusEntity.builder()
                .id(this.id)
                .payload(this.payload)
                .status(newStatus)
                .details(description)
                .createdAt(this.createdAt)
                .build();
    }
}
