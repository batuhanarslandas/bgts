package com.example.virtual_thread_service.service;

import com.example.virtual_thread_service.entity.RequestStatusEntity;
import com.example.virtual_thread_service.mapper.RequestMapper;
import com.example.virtual_thread_service.model.RequestDomainModel;
import com.example.virtual_thread_service.repository.RequestStatusRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RequestService {

    private final String THIRD_PARTY_BASE_API = "http://localhost:5000";

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final RequestStatusRepository repository;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final Counter submitCounter;
    private final Counter statusCounter;
    private final Counter errorCounter;
    private final Timer processingTimer;

    public RequestService(RequestStatusRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;

        this.submitCounter = Counter.builder("requests_submit_total")
                .description("Total number of /submit requests")
                .register(meterRegistry);

        this.statusCounter = Counter.builder("requests_status_total")
                .description("Total number of /status requests")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("requests_failed_total")
                .description("Total number of failed requests")
                .register(meterRegistry);

        this.processingTimer = Timer.builder("request_processing_duration")
                .description("Duration of processing 3rd party request")
                .register(meterRegistry);
    }

    public HttpResponse<String> callThirdPartyMockService() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5000/mock"))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Long submitRequest(String jsonPayload) {
        submitCounter.increment();
        logger.info("Submit request received. Payload: {}", jsonPayload);

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity(
                null,
                jsonPayload,
                RequestStatusEntity.Status.PENDING,
                "Waiting for processing",
                Instant.now()
        );

        repository.save(requestStatusEntity);

        executor.submit(() -> {
            processingTimer.record(() -> {
                try {
                    logger.info("Processing started for ID: {}", requestStatusEntity.getId());

                    // 3rd party API call

                    HttpResponse<String> response = callThirdPartyMockService();

                    if (response.statusCode() == 200) {
                        String responseBody = response.body();
                        logger.info("3rd party response received: {}", responseBody);

                        RequestStatusEntity updated = new RequestStatusEntity(
                                requestStatusEntity.getId(),
                                jsonPayload,
                                RequestStatusEntity.Status.COMPLETED,
                                responseBody,
                                requestStatusEntity.getCreatedAt()
                        );

                        repository.save(updated);
                        logger.info("Processing completed for ID: {}", requestStatusEntity.getId());
                    } else {
                        throw new RuntimeException("3rd party call failed with status code: " + response.statusCode());
                    }

                } catch (Exception e) {
                    logger.error("Processing failed: {}", e.getMessage());
                    RequestStatusEntity failed = new RequestStatusEntity(
                            requestStatusEntity.getId(),
                            jsonPayload,
                            RequestStatusEntity.Status.FAILED,
                            "Error occurred: " + e.getMessage(),
                            requestStatusEntity.getCreatedAt()
                    );
                    repository.save(failed);
                    errorCounter.increment();
                }
            });
        });

        return requestStatusEntity.getId();
    }


    public Optional<RequestDomainModel> getStatus(Long id) {
        statusCounter.increment();
        logger.info("Fetching status for ID: {}", id);
        return repository.findById(id).map(RequestMapper::toDomain);
    }
}
