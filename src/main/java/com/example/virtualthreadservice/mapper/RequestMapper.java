package com.example.virtualthreadservice.mapper;

import com.example.virtualthreadservice.dto.RequestResponseDTO;
import com.example.virtualthreadservice.entity.RequestStatusEntity;
import com.example.virtualthreadservice.model.RequestDomainModel;

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
