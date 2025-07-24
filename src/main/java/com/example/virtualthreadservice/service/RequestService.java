package com.example.virtualthreadservice.service;

import com.example.virtualthreadservice.entity.RequestStatusEntity;
import com.example.virtualthreadservice.mapper.RequestMapper;
import com.example.virtualthreadservice.model.RequestDomainModel;
import com.example.virtualthreadservice.repository.RequestStatusRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public final class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final String THIRD_PARTY_URL;

    private final RequestStatusRepository repository;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final Counter submitCounter;
    private final Counter statusCounter;
    private final Counter errorCounter;
    private final Timer requestTimer;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(Executors.newFixedThreadPool(200)) // max 200 parallel
            .build();

    public RequestService(RequestStatusRepository repository, MeterRegistry meterRegistry, @Value("${third.party.url}") String THIRD_PARTY_URL) {
        this.repository = repository;
        this.THIRD_PARTY_URL = THIRD_PARTY_URL;

        this.submitCounter = Counter.builder("requests_submit_total")
                .description("Total number of /submit requests")
                .register(meterRegistry);

        this.statusCounter = Counter.builder("requests_status_total")
                .description("Total number of /status requests")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("requests_failed_total")
                .description("Total number of failed requests")
                .register(meterRegistry);

        this.requestTimer = Timer.builder("request_processing_duration")
                .description("Processing time for requests")
                .publishPercentiles(0.95, 0.99) // P95 ve P99
                .publishPercentileHistogram()    // Prometheus için histogram
                .register(meterRegistry);

    }

    /**
     * Bu metod, dıştaki kendi oluşturmuş olduğumuz Python API'siyle
     * iletişim kurmak için kullanılır.
     */
    public CompletableFuture<HttpResponse<String>> callThirdPartyMockService(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(THIRD_PARTY_URL))
                .timeout(Duration.ofSeconds(10)) // HTTP timeout
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Bu metod, gelen JSON verisini veritabanına kaydeder ve işleme almak için bir thread başlatır.
     * İsteğin ID'si hemen geri döner, işleme arkada devam eder.
     */
    public Long submitRequest(Map<String, Object> jsonPayload) {
        submitCounter.increment();
        logger.info("Submit request received. Payload: {}", jsonPayload);

        RequestStatusEntity savedEntity = saveInitialRequest(jsonPayload);

        processRequestAsync(savedEntity);

        return savedEntity.getId(); // İstek kodu 202 Accepted olarak döner
    }

    // İsteği veritabanına PENDING durumunda kaydeder
    private RequestStatusEntity saveInitialRequest(Map<String, Object> jsonPayload) {
        RequestStatusEntity requestStatusEntity = RequestStatusEntity.builder()
                .id(null)  // optional
                .payload(jsonPayload)
                .status(RequestStatusEntity.Status.PENDING)
                .details("Waiting for processing")
                .createdAt(Instant.now())
                .build();

        return repository.save(requestStatusEntity);
    }

    /**
     * Bu metod, verilen isteği asenkron olarak bir virtual thread içinde işler.
     * Böylece thread havuzu binlerce isteği paralel işleyebilir.
     */
    private void processRequestAsync(RequestStatusEntity requestEntity) {
        executor.submit(() -> {
            // İsteğin durumu "PROCESSING" olarak güncellenir
            RequestStatusEntity processing = requestEntity.withStatus(RequestStatusEntity.Status.PROCESSING, "Processing started");
            repository.save(processing);
            logger.info("Request ID {} set to PROCESSING", processing.getId());

            processRequest(processing);
        });
    }

    /**
     * Mock servisine istek atılır, dönen cevaba göre işlem durumu güncellenir.
     */
    private void processRequest(RequestStatusEntity requestEntity) {
        requestTimer.record(() -> {

        logger.info("Processing started for ID: {}", requestEntity.getId());
        // Mock servisini çağır
        callThirdPartyMockService().thenAccept(response -> {
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                logger.info("3rd party response received: {}", responseBody);

                RequestStatusEntity updated = requestEntity.withStatus(RequestStatusEntity.Status.COMPLETED, responseBody);
                repository.save(updated);
                logger.info("Processing completed for ID: {}", requestEntity.getId());
            } else {
                failRequest(requestEntity, "3rd party call failed with status code: " + response.statusCode());
            }
        }).exceptionally(ex -> {
            failRequest(requestEntity, "Error occurred: " + ex.getMessage());
            return null;
        });
        });
    }

    private void failRequest(RequestStatusEntity entity, String reason) {
        logger.error("Processing failed for ID {}: {}", entity.getId(), reason);
        RequestStatusEntity failed = entity.withStatus(RequestStatusEntity.Status.FAILED, reason);
        repository.save(failed);
        errorCounter.increment();
    }
    /**
     * Verilen ID’ye göre requestStatus değeri döndürülür.
     */
    public Optional<RequestDomainModel> getStatus(Long id) {
        statusCounter.increment();
        logger.info("Fetching status for ID: {}", id);
        return repository.findById(id).map(RequestMapper::toDomain);
    }
}