package com.danielbyrne.daftsearch.domain.mappers;

import com.danielbyrne.daftsearch.domain.Property;
import com.danielbyrne.daftsearch.domain.model.PropertyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyMapper {

    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    PropertyDTO propertyToPropertyDTO(Property property);

    Property propertyDTOtoProperty(PropertyDTO propertyDTO);
}
