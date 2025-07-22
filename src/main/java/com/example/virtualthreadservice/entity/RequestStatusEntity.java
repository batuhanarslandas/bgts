package com.example.virtualthreadservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;

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

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    private final Status status;

    @Column(nullable = false)
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
