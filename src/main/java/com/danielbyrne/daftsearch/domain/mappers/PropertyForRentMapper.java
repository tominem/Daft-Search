package com.danielbyrne.daftsearch.domain.mappers;

import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyForRentMapper {

    PropertyForRentMapper INSTANCE = Mappers.getMapper(PropertyForRentMapper.class);

    PropertyForRentDTO propertyForRentToPropertyForRentDTO(PropertyForRent propertyForRent);

    PropertyForRent propertyForRentDTOToPropertyForRent(PropertyForRentDTO propertyForRentDTO);
}