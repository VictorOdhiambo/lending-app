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
import org.junit.jupiter.api.BeforeEach;
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

    private Product product;
    private ProductDTO productDTO;
    private Charge charge;
    private ChargeDTO chargeDTO;
    private ProductChargeMapping productChargeMapping;
    private ProductChargeMappingDTO productChargeMappingDTO;
    private UUID productId;
    private UUID chargeId;
    private UUID productChargeMappingId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        chargeId = UUID.randomUUID();
        productChargeMappingId = UUID.randomUUID();

        product = new Product(UUID.randomUUID(), "Okoa Loan", "A micro-loan", true, 5,
                LocalDateTime.now());
        productDTO = new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.isEnabled(),
                product.getGracePeriod(), product.getCreatedDate());


        productChargeMapping = new ProductChargeMapping(productChargeMappingId, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        productChargeMappingDTO = new ProductChargeMappingDTO(productChargeMapping.getId(), productChargeMapping.getProductId(),
                productChargeMapping.getChargeId(), productChargeMapping.getCreatedDate());

        charge = new Charge(
                chargeId, "Processing Fee", true, true, false, 200,
                LocalDateTime.now()
        );
        chargeDTO = new ChargeDTO(
                charge.getId(), charge.getName(), charge.isEnabled(), charge.isUpfront(),
                charge.isPenalty(), charge.getAmount(), charge.getCreatedDate()
        );
    }

    @Test
    void addProduct_shouldReturnProductDto() {

        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        StepVerifier.create(productService.addProduct(productDTO))
                .expectNext(productDTO)
                .verifyComplete();
    }

    @Test
    void addCharge_shouldReturnChargeDto() {
        when(chargeMapper.toEntity(chargeDTO)).thenReturn(charge);
        when(chargeRepository.save(charge)).thenReturn(Mono.just(charge));
        when(chargeMapper.toDto(charge)).thenReturn(chargeDTO);

        StepVerifier.create(productService.addCharge(chargeDTO))
                .expectNext(chargeDTO)
                .verifyComplete();
    }

    @Test
    void addProductChargeMapping_shouldReturnMappingDto() {

        when(mappingMapper.toEntity(productChargeMappingDTO)).thenReturn(productChargeMapping);
        when(mappingRepository.save(productChargeMapping)).thenReturn(Mono.just(productChargeMapping));
        when(mappingMapper.toDto(productChargeMapping)).thenReturn(productChargeMappingDTO);

        StepVerifier.create(productService.addProductChargeMapping(productChargeMappingDTO))
                .expectNext(productChargeMappingDTO)
                .verifyComplete();
    }

    @Test
    void findProductById_shouldReturnProductDto() {

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        StepVerifier.create(productService.findProductById(productId))
                .expectNext(productDTO)
                .verifyComplete();
    }

    @Test
    void findProductChargesByProductId_shouldReturnFluxOfCharges() {

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(mappingRepository.findProductChargeMappingByProductId(productId))
                .thenReturn(Flux.just(productChargeMapping));
        when(chargeRepository.findById(productChargeMapping.getChargeId()))
                .thenReturn(Mono.just(charge));
        when(chargeMapper.toDto(charge)).thenReturn(chargeDTO);

        StepVerifier.create(productService.findProductChargesByProductId(productId))
                .expectNext(chargeDTO)
                .verifyComplete();
    }

    @Test
    void findAllProducts_shouldReturnFluxOfProducts() {

        when(productRepository.findAll()).thenReturn(Flux.just(product));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        StepVerifier.create(productService.findAllProducts())
                .expectNext(productDTO)
                .verifyComplete();
    }

    @Test
    void findAllCharges_shouldReturnFluxOfCharges() {
        when(chargeRepository.findAll()).thenReturn(Flux.just(charge));
        when(chargeMapper.toDto(charge)).thenReturn(chargeDTO);

        StepVerifier.create(productService.findAllCharges())
                .expectNext(chargeDTO)
                .verifyComplete();
    }
}

