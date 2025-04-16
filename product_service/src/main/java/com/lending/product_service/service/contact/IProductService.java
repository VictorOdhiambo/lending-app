package com.lending.product_service.service.contact;

import com.lending.product_service.dto.ChargeDTO;
import com.lending.product_service.dto.ProductChargeMappingDTO;
import com.lending.product_service.dto.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IProductService {
    Mono<ProductDTO> addProduct(ProductDTO productDTO);
    Mono<ChargeDTO> addCharge(ChargeDTO chargeDTO);
    Mono<ProductChargeMappingDTO> addProductChargeMapping(ProductChargeMappingDTO productChargeMappingDTO);
    Mono<ProductDTO> findProductById(UUID productId);
    Flux<ChargeDTO> findProductChargesByProductId(UUID productId);
    Flux<ProductDTO> findAllProducts();
    Flux<ChargeDTO> findAllCharges();
}
