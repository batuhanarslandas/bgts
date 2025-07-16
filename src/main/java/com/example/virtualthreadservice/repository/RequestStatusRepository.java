package com.example.virtualthreadservice.repository;

import com.example.virtualthreadservice.entity.RequestStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestStatusRepository extends JpaRepository<RequestStatusEntity, Long> {
}
