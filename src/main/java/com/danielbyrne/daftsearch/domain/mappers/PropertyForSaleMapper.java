package com.danielbyrne.daftsearch.domain.mappers;

import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyForSaleMapper {

    PropertyForSaleMapper INSTANCE = Mappers.getMapper(PropertyForSaleMapper.class);

    PropertyForRentDTO propertyForSaleToPropertyForSaleDTO(PropertyForSale propertyForSale);

    PropertyForSale propertyForSaleDTOtoPropertyForSale(PropertyForSaleDTO propertyForSaleDTO);
}
