package com.example.virtual_thread_service.dto;

import java.time.Instant;

public record RequestResponseDTO(Long id, String payload, String status, Instant createdAt) {
}
