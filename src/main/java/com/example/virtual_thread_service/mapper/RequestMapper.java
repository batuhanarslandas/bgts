package com.example.virtual_thread_service.mapper;

import com.example.virtual_thread_service.dto.RequestResponseDTO;
import com.example.virtual_thread_service.entity.RequestStatusEntity;
import com.example.virtual_thread_service.model.RequestDomainModel;

public class RequestMapper {

    public static RequestDomainModel toDomain(RequestStatusEntity entity) {
        return RequestDomainModel.of(
                entity.getId(),
                entity.getPayload(),
                entity.getStatus().name(),
                entity.getCreatedAt()
        );
    }

    public static RequestResponseDTO toDTO(RequestDomainModel domain) {
        return new RequestResponseDTO(
                domain.getId(),
                domain.getPayload(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
