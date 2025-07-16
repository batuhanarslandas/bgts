package com.example.virtual_thread_service.model;

import lombok.Getter;

import java.time.Instant;

@Getter
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

}
