package com.lending.loan_service.mapper;

import com.lending.loan_service.dto.LoanDTO;
import com.lending.loan_service.model.Loan;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    LoanDTO toDto(Loan loan);

    @InheritInverseConfiguration
    Loan toEntity(LoanDTO loanDTO);

}
