package com.example.virtual_thread_service.service;

import com.example.virtual_thread_service.entity.RequestStatus;
import com.example.virtual_thread_service.repository.RequestStatusRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RequestService {

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

    public Long submitRequest(String jsonPayload) {
        submitCounter.increment();

        RequestStatus request = new RequestStatus();
        request.setStatus(RequestStatus.Status.PENDING);
        request.setDetails(jsonPayload);
        repository.save(request);

        executor.submit(() -> {
            processingTimer.record(() -> {
                try {
                    Thread.sleep(5000); // Mock 3rd party call
                    request.setStatus(RequestStatus.Status.COMPLETED);
                    request.setDetails("3rd party response here");
                    repository.save(request);
                } catch (InterruptedException e) {
                    request.setStatus(RequestStatus.Status.FAILED);
                    repository.save(request);
                    errorCounter.increment();
                }
            });
        });

        return request.getId();
    }

    public Optional<RequestStatus> getStatus(Long id) {
        statusCounter.increment();
        return repository.findById(id);
    }
}
