package com.example.virtualthreadservice.controller;

import com.example.virtualthreadservice.dto.RequestDTO;
import com.example.virtualthreadservice.mapper.RequestMapper;
import com.example.virtualthreadservice.service.RequestService;
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
    public ResponseEntity<Map<String, Object>> submit(@RequestBody RequestDTO requestDTO) {
        Long id = service.submitRequest(requestDTO.payload().toString());
        return ResponseEntity.accepted().body(Map.of("requestId", id));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable Long id) {
        return service.getStatus(id)
                .map(RequestMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
