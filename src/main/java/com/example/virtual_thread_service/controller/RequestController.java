package com.example.virtual_thread_service.controller;

import com.example.virtual_thread_service.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RequestController {

    private final RequestService service;

    public RequestController(RequestService service) {
        this.service = service;
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submit(@RequestBody Map<String, Object> payload) {
        Long id = service.submitRequest(payload.toString());
        return ResponseEntity.accepted().body(Map.of("requestId", id));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable Long id) {
        return service.getStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
