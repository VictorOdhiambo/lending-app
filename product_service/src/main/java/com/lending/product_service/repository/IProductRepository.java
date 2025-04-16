package com.lending.product_service.repository;

import com.lending.product_service.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface IProductRepository extends ReactiveCrudRepository<Product, UUID> {
}
