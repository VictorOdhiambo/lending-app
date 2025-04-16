package com.lending.product_service.repository;

import com.lending.product_service.model.Charge;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface IChargeRepository extends ReactiveCrudRepository<Charge, UUID> {
}
