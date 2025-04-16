package com.lending.product_service.mapper;

import com.lending.product_service.dto.ChargeDTO;
import com.lending.product_service.model.Charge;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChargeMapper {
    ChargeMapper INSTANCE = Mappers.getMapper(ChargeMapper.class);

    ChargeDTO toDto(Charge charge);

    @InheritInverseConfiguration
    Charge toEntity(ChargeDTO chargeDTO);
}
