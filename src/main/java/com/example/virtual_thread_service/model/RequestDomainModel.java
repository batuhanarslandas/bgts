package com.example.virtual_thread_service.model;

import java.time.Instant;

public final class RequestDomainModel {
    private final Long id;
    private final String payload;
    private final String status;
    private final Instant createdAt;

    private RequestDomainModel(Long id, String payload, String status, Instant createdAt) {
        this.id = id;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static RequestDomainModel of(Long id, String payload, String status, Instant createdAt) {
        return new RequestDomainModel(id, payload, status, createdAt);
    }

    public Long getId() { return id; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
