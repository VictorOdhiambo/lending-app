package com.lending.customer_service.mapper;

import com.lending.customer_service.dto.CustomerDTO;
import com.lending.customer_service.model.Customer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO toDto(Customer customer);

    @InheritInverseConfiguration
    Customer toEntity(CustomerDTO customerDTO);
}
