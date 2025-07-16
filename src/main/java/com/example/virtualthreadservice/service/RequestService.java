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

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);
    private static final String THIRD_PARTY_URL = "http://localhost:5000/mock";

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
                .uri(URI.create(THIRD_PARTY_URL))
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
            RequestStatusEntity processing = requestStatusEntity.withStatus(RequestStatusEntity.Status.PROCESSING, "Processing started");
            repository.save(processing);
            logger.info("Request ID {} set to PROCESSING", processing.getId());

            processingTimer.record(() -> {
                try {
                    logger.info("Processing started for ID: {}", requestStatusEntity.getId());

                    // 3rd party API call
                    HttpResponse<String> response = callThirdPartyMockService();

                    if (response.statusCode() == 200) {
                        String responseBody = response.body();
                        logger.info("3rd party response received: {}", responseBody);

                        RequestStatusEntity updated = requestStatusEntity.withStatus(RequestStatusEntity.Status.COMPLETED, responseBody);

                        repository.save(updated);
                        logger.info("Processing completed for ID: {}", requestStatusEntity.getId());
                    } else {
                        throw new RuntimeException("3rd party call failed with status code: " + response.statusCode());
                    }

                } catch (Exception e) {
                    logger.error("Processing failed: {}", e.getMessage());
                    RequestStatusEntity failed = requestStatusEntity.withStatus(RequestStatusEntity.Status.FAILED, "Error occurred: " + e.getMessage());
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
