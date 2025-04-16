package com.lending.product_service.mapper;

import com.lending.product_service.dto.ProductDTO;
import com.lending.product_service.model.Product;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDto(Product entity);

    @InheritInverseConfiguration
    Product toEntity(ProductDTO dto);
}
