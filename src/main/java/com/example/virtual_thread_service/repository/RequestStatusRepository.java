package com.example.virtual_thread_service.repository;

import com.example.virtual_thread_service.entity.RequestStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestStatusRepository extends JpaRepository<RequestStatusEntity, Long> {
}
