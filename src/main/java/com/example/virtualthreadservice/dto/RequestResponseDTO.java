package com.example.virtualthreadservice.dto;

import java.time.Instant;

public record RequestResponseDTO(Long id, String payload, String status, Instant createdAt) {
}
