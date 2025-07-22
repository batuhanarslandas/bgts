package com.example.virtualthreadservice.controller;

import com.example.virtualthreadservice.dto.RequestDTO;
import com.example.virtualthreadservice.mapper.RequestMapper;
import com.example.virtualthreadservice.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public final class RequestController {

    private final RequestService service;

    // (Dependency Injection)
    public RequestController(RequestService service) {
        this.service = service;
    }

    /**
     * /submit endpoint'i: Yeni bir iş isteği alır ve işlem kuyruğuna gönderir.
     * JSON formatındaki payload'ı alıp işleme başlatır, requestId ile geri döner.
     * Yanıt 202 Accepted — işlem henüz tamamlanmadı olarak geri dönmektedir.
     */
    @Operation(summary = "Yeni iş gönder", description = "Bir JSON payload alır ve işleme alır.")
    @ApiResponse(responseCode = "202", description = "İstek kabul edildi")
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submit(@RequestBody RequestDTO requestDTO) {
        Long id = service.submitRequest(requestDTO.payload());
        return ResponseEntity.accepted().body(Map.of("requestId", id));
    }

    /**
     * /status/{id} endpoint'i: Daha önce gönderilmiş bir isteğin durumunu döner.
     * Eğer istek bulunamazsa 404 Not Found yanıtı verir.
     * Bulunursa DTO'ya çevrilir ve 200 OK ile döner.
     */
    @Operation(summary = "İşin durumunu getir", description = "ID ile durum sorgusu yapılır.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Durum bulundu"),
            @ApiResponse(responseCode = "404", description = "İstek bulunamadı")
    })
    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable Long id) {
        return service.getStatus(id)
                .map(RequestMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
