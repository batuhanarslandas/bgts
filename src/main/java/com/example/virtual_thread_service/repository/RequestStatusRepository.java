package com.example.virtual_thread_service.repository;

import com.example.virtual_thread_service.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {
}
