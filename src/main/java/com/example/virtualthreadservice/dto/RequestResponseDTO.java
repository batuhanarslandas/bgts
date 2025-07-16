package com.example.virtualthreadservice.dto;

import java.time.Instant;

/**
 * RequestResponseDTO, /status/{id} endpoint'inden dönen cevabı temsil eder.
 */
public record RequestResponseDTO(Long id, String payload, String status, Instant createdAt) {
}
