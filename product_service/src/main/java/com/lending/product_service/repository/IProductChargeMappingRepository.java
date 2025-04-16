package com.lending.product_service.repository;

import com.lending.product_service.model.ProductChargeMapping;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface IProductChargeMappingRepository extends ReactiveCrudRepository<ProductChargeMapping, UUID> {
    Flux<ProductChargeMapping> findProductChargeMappingByProductId(UUID productId);
}
