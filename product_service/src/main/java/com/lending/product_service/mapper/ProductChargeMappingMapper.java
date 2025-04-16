package com.lending.product_service.mapper;

import com.lending.product_service.dto.ProductChargeMappingDTO;
import com.lending.product_service.model.ProductChargeMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductChargeMappingMapper {
    ProductChargeMappingMapper INSTANCE = Mappers.getMapper(ProductChargeMappingMapper.class);

    ProductChargeMappingDTO toDto(ProductChargeMapping productChargeMapping);

    @InheritInverseConfiguration
    ProductChargeMapping toEntity(ProductChargeMappingDTO productChargeMappingDTO);
}
