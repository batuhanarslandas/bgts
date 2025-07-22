package com.example.virtualthreadservice.dto;

import java.time.Instant;
import java.util.Map;

/**
 * RequestResponseDTO, /status/{id} endpoint'inden dönen cevabı temsil eder.
 */
public record RequestResponseDTO(Long id, Map<String, Object> payload, String status, Instant createdAt) {
}
