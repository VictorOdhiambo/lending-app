package com.lending.product_service;

import com.lending.product_service.dto.ChargeDTO;
import com.lending.product_service.dto.ProductChargeMappingDTO;
import com.lending.product_service.dto.ProductDTO;
import com.lending.product_service.mapper.ChargeMapper;
import com.lending.product_service.mapper.ProductChargeMappingMapper;
import com.lending.product_service.mapper.ProductMapper;
import com.lending.product_service.model.Charge;
import com.lending.product_service.model.Product;
import com.lending.product_service.model.ProductChargeMapping;
import com.lending.product_service.repository.IChargeRepository;
import com.lending.product_service.repository.IProductChargeMappingRepository;
import com.lending.product_service.repository.IProductRepository;
import com.lending.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private IChargeRepository chargeRepository;

    @Mock
    private IProductChargeMappingRepository mappingRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ChargeMapper chargeMapper;

    @Mock
    private ProductChargeMappingMapper mappingMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct_shouldReturnProductDto() {
        Product entity = new Product(UUID.randomUUID(), "Okoa Loan", "A micro-loan", true, 5,
                LocalDateTime.now());
        ProductDTO dto = new ProductDTO(entity.getId(), entity.getName(), entity.getDescription(), entity.isEnabled(),
                entity.getGracePeriod(), entity.getCreatedDate());

        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));
        when(productMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.addProduct(dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void addCharge_shouldReturnChargeDto() {
        Charge entity = new Charge(
                UUID.randomUUID(), "Processing Fee", true, true, false, 200,
                LocalDateTime.now()
        );
        ChargeDTO dto = new ChargeDTO(
                entity.getId(), entity.getName(), entity.isEnabled(), entity.isUpfront(),
                entity.isPenalty(), entity.getAmount(), entity.getCreatedDate()
        );

        when(chargeMapper.toEntity(dto)).thenReturn(entity);
        when(chargeRepository.save(entity)).thenReturn(Mono.just(entity));
        when(chargeMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.addCharge(dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void addProductChargeMapping_shouldReturnMappingDto() {
        ProductChargeMapping entity = new ProductChargeMapping(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        ProductChargeMappingDTO dto = new ProductChargeMappingDTO(entity.getId(), entity.getProductId(), entity.getChargeId(), entity.getCreatedDate());

        when(mappingMapper.toEntity(dto)).thenReturn(entity);
        when(mappingRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mappingMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.addProductChargeMapping(dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void findProductById_shouldReturnProductDto() {
        UUID id = UUID.randomUUID();
        Product entity = new Product(UUID.randomUUID(), "Okoa Loan", "A micro-loan", true, 5,
                LocalDateTime.now());
        ProductDTO dto = new ProductDTO(entity.getId(), entity.getName(), entity.getDescription(), entity.isEnabled(),
                entity.getGracePeriod(), entity.getCreatedDate());

        when(productRepository.findById(id)).thenReturn(Mono.just(entity));
        when(productMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.findProductById(id))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void findProductChargesByProductId_shouldReturnFluxOfCharges() {
        UUID productId = UUID.randomUUID();
        Product product = new Product(); product.setId(productId);
        ProductChargeMapping mapping = new ProductChargeMapping();
        mapping.setChargeId(UUID.randomUUID());

        Charge entity = new Charge(
                UUID.randomUUID(), "Processing Fee", true, true, false, 200,
                LocalDateTime.now()
        );
        ChargeDTO dto = new ChargeDTO(
                entity.getId(), entity.getName(), entity.isEnabled(), entity.isUpfront(),
                entity.isPenalty(), entity.getAmount(), entity.getCreatedDate()
        );

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(mappingRepository.findProductChargeMappingByProductId(productId))
                .thenReturn(Flux.just(mapping));
        when(chargeRepository.findById(mapping.getChargeId()))
                .thenReturn(Mono.just(entity));
        when(chargeMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.findProductChargesByProductId(productId))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void findAllProducts_shouldReturnFluxOfProducts() {
        Product entity = new Product(UUID.randomUUID(), "Okoa Loan", "A micro-loan", true, 5,
                LocalDateTime.now());
        ProductDTO dto = new ProductDTO(entity.getId(), entity.getName(), entity.getDescription(), entity.isEnabled(),
                entity.getGracePeriod(), entity.getCreatedDate());

        when(productRepository.findAll()).thenReturn(Flux.just(entity));
        when(productMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.findAllProducts())
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void findAllCharges_shouldReturnFluxOfCharges() {
        Charge entity = new Charge(
                UUID.randomUUID(), "Processing Fee", true, true, false, 200,
                LocalDateTime.now()
        );
        ChargeDTO dto = new ChargeDTO(
                entity.getId(), entity.getName(), entity.isEnabled(), entity.isUpfront(),
                entity.isPenalty(), entity.getAmount(), entity.getCreatedDate()
        );

        when(chargeRepository.findAll()).thenReturn(Flux.just(entity));
        when(chargeMapper.toDto(entity)).thenReturn(dto);

        StepVerifier.create(productService.findAllCharges())
                .expectNext(dto)
                .verifyComplete();
    }
}

