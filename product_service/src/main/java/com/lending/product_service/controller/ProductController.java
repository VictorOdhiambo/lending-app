package com.lending.product_service.controller;

import com.lending.product_service.dto.ChargeDTO;
import com.lending.product_service.dto.ProductChargeMappingDTO;
import com.lending.product_service.dto.ProductDTO;
import com.lending.product_service.service.contact.IProductService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private Validator validator;

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Mono<ResponseEntity<ProductDTO>> addProduct(@RequestBody Mono<ProductDTO> productDTO) {
        return productDTO
                .doOnNext(this::validate)
                .flatMap(productService::addProduct)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/add-charge")
    public Mono<ResponseEntity<ChargeDTO>> addCharge(@RequestBody Mono<ChargeDTO> productDTO) {
        return productDTO
                .doOnNext(this::validate)
                .flatMap(productService::addCharge)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/add-product-charge-mapping")
    public Mono<ResponseEntity<ProductChargeMappingDTO>> addProductChargeMapping(@RequestBody Mono<ProductChargeMappingDTO> productDTO) {
        return productDTO
                .doOnNext(this::validate)
                .flatMap(productService::addProductChargeMapping)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<ProductDTO>> getProductById(@PathVariable("id") String id){
        return ResponseEntity.ok(productService.findProductById(UUID.fromString(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<Flux<ProductDTO>> getAllProducts(){
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @GetMapping("/find-all-charges")
    public ResponseEntity<Flux<ChargeDTO>> getAllCharges(){
        return ResponseEntity.ok(productService.findAllCharges());
    }

    @GetMapping("/find-charges-by-product-id/{id}")
    public ResponseEntity<Flux<ChargeDTO>> findProductChargesByProductId(String productId){
        return ResponseEntity.ok(productService.findProductChargesByProductId(UUID.fromString(productId)));
    }

    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
