package com.lending.product_service.service;

import com.lending.product_service.dto.ChargeDTO;
import com.lending.product_service.dto.ProductChargeMappingDTO;
import com.lending.product_service.dto.ProductDTO;
import com.lending.product_service.exception.ChargeException;
import com.lending.product_service.exception.ProductChargeException;
import com.lending.product_service.exception.ProductException;
import com.lending.product_service.mapper.ChargeMapper;
import com.lending.product_service.mapper.ProductChargeMappingMapper;
import com.lending.product_service.mapper.ProductMapper;
import com.lending.product_service.model.Charge;
import com.lending.product_service.model.Product;
import com.lending.product_service.model.ProductChargeMapping;
import com.lending.product_service.repository.IChargeRepository;
import com.lending.product_service.repository.IProductChargeMappingRepository;
import com.lending.product_service.repository.IProductRepository;
import com.lending.product_service.service.contact.IProductService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProductService implements IProductService {

    private final IProductRepository productRepository;
    private final IChargeRepository chargeRepository;
    private final IProductChargeMappingRepository productChargeMappingRepository;
    private final ProductMapper productMapper;
    private final ChargeMapper chargeMapper;
    private final ProductChargeMappingMapper productChargeMapper;

    public ProductService(IProductRepository repository, IChargeRepository chargeRepository,
                          IProductChargeMappingRepository productChargeMappingRepository, ProductMapper productMapper,
                          ChargeMapper chargeMapper, ProductChargeMappingMapper productChargeMapper){
        this.productRepository = repository;
        this.chargeRepository = chargeRepository;
        this.productChargeMappingRepository = productChargeMappingRepository;
        this.productMapper = productMapper;
        this.chargeMapper = chargeMapper;
        this.productChargeMapper = productChargeMapper;
    }

    @Override
    public Mono<ProductDTO> addProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        return productRepository.save(product)
                .flatMap(persisted -> Mono.just(productMapper.toDto(persisted)))
                .switchIfEmpty(Mono.error(new ProductException("Error when creating product")));
    }

    @Override
    public Mono<ChargeDTO> addCharge(ChargeDTO chargeDTO) {
        Charge charge = chargeMapper.toEntity(chargeDTO);
        return chargeRepository.save(charge)
                .flatMap(persisted -> Mono.just(chargeMapper.toDto(persisted)))
                .switchIfEmpty(Mono.error(new ChargeException("Error when creating charge")));
    }

    @Override
    public Mono<ProductChargeMappingDTO> addProductChargeMapping(ProductChargeMappingDTO productChargeMappingDTO) {
        ProductChargeMapping productCharge = productChargeMapper.toEntity(productChargeMappingDTO);
        return productChargeMappingRepository.save(productCharge)
                .flatMap(persisted -> Mono.just(productChargeMapper.toDto(persisted)))
                .switchIfEmpty(Mono.error(new ProductChargeException("Error when creating product-charge mapping")));
    }

    @Override
    public Mono<ProductDTO> findProductById(UUID productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductException("Error retrieving product details")))
                .flatMap(persisted -> Mono.just(productMapper.toDto(persisted)));
    }

    @Override
    public Flux<ChargeDTO> findProductChargesByProductId(UUID productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductException("Error retrieving product details")))
                .flatMapMany(product ->
                        productChargeMappingRepository.findProductChargeMappingByProductId(product.getId())
                )
                .flatMap(mapping ->
                        chargeRepository.findById(mapping.getChargeId())
                                .map(chargeMapper::toDto)
                );
    }


    @Override
    public Flux<ProductDTO> findAllProducts() {
        return productRepository.findAll()
                .switchIfEmpty(Mono.error(new ProductException("Error retrieving products")))
                .flatMap(persisted -> Mono.just(productMapper.toDto(persisted)));
    }

    @Override
    public Flux<ChargeDTO> findAllCharges() {
        return chargeRepository.findAll()
                .switchIfEmpty(Mono.error(new ChargeException("Error retrieving charges")))
                .flatMap(persisted -> Mono.just(chargeMapper.toDto(persisted)));
    }
}
