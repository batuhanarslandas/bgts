package com.example.virtualthreadservice.mapper;

import com.example.virtualthreadservice.dto.RequestResponseDTO;
import com.example.virtualthreadservice.entity.RequestStatusEntity;
import com.example.virtualthreadservice.model.RequestDomainModel;

/**
 * Mapper sınıfı: Katmanlar arası veri dönüşümünü merkezi olarak sağlar.
 * Entity  => Domain, DTO dönüşümleri burada yapılır.
 */
public final class RequestMapper {

    /**
     * Veritabanı varlığı olan Entity nesnesini, Domain modele dönüştürür.
     */
    public static RequestDomainModel toDomain(RequestStatusEntity entity) {
        return RequestDomainModel.of(
                entity.getId(),
                entity.getPayload(),
                entity.getStatus().name(),
                entity.getCreatedAt()
        );
    }

    /**
     * Domain modelini, dış API’ye döneceğimiz DTO nesnesine çevirir.
     */
    public static RequestResponseDTO toDTO(RequestDomainModel domain) {
        return new RequestResponseDTO(
                domain.getId(),
                domain.getPayload(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
