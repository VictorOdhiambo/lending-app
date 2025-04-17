package com.lending.loan_service.mapper;

import com.lending.loan_service.dto.RepaymentRequestDTO;
import com.lending.loan_service.model.RepaymentRequest;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RepaymentRequestMapper {
    RepaymentRequestMapper INSTANCE = Mappers.getMapper(RepaymentRequestMapper.class);

    RepaymentRequestDTO toDto(RepaymentRequest repaymentRequest);

    @InheritInverseConfiguration
    RepaymentRequest toEntity(RepaymentRequestDTO repaymentRequestDTO);
}
